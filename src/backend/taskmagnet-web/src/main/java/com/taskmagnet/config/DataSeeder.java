package com.taskmagnet.config;

import com.taskmagnet.entity.Category;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.Role;
import com.taskmagnet.entity.Task;
import com.taskmagnet.entity.TaskStatus;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.Priority;
import com.taskmagnet.enums.ProjectStatus;
import com.taskmagnet.enums.RoleName;
import com.taskmagnet.repository.CategoryRepository;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.RoleRepository;
import com.taskmagnet.repository.TaskRepository;
import com.taskmagnet.repository.TaskStatusRepository;
import com.taskmagnet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository,
                      UserRepository userRepository,
                      ProjectRepository projectRepository,
                      TaskRepository taskRepository,
                      TaskStatusRepository taskStatusRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedCategories();
        seedRoles();
        List<User> users = seedUsers();
        seedUserRoles();
        List<Project> projects = seedProjects(users);
        seedTaskStatuses(users, projects);
        seedTasks(users, projects);
    }

    // -------------------------------------------------------------------------
    // Roles
    // -------------------------------------------------------------------------
    private void seedRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already seeded — skipping.");
            return;
        }

        roleRepository.save(new Role(RoleName.ROLE_ADMIN,     "Full system access"));
        roleRepository.save(new Role(RoleName.ROLE_MANAGER,   "Project and team management"));
        roleRepository.save(new Role(RoleName.ROLE_DEVELOPER, "Development tasks"));
        roleRepository.save(new Role(RoleName.ROLE_QA,        "Quality assurance and testing"));
        roleRepository.save(new Role(RoleName.ROLE_DESIGNER,  "UI/UX design"));
        roleRepository.save(new Role(RoleName.ROLE_VIEWER,    "Read-only access"));
        log.info("Seeded 6 roles.");
    }

    // -------------------------------------------------------------------------
    // User → Role assignments (idempotent, handles existing users)
    // -------------------------------------------------------------------------
    private void seedUserRoles() {
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin == null || !admin.getRoles().isEmpty()) {
            log.info("User roles already assigned — skipping.");
            return;
        }

        Role adminRole     = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role managerRole   = roleRepository.findByName(RoleName.ROLE_MANAGER).orElseThrow();
        Role developerRole = roleRepository.findByName(RoleName.ROLE_DEVELOPER).orElseThrow();
        Role qaRole        = roleRepository.findByName(RoleName.ROLE_QA).orElseThrow();
        Role designerRole  = roleRepository.findByName(RoleName.ROLE_DESIGNER).orElseThrow();

        admin.getRoles().add(adminRole);
        admin.getRoles().add(managerRole);
        userRepository.save(admin);

        userRepository.findByUsername("alice.johnson").ifPresent(u -> {
            u.getRoles().add(developerRole);
            userRepository.save(u);
        });
        userRepository.findByUsername("bob.smith").ifPresent(u -> {
            u.getRoles().add(qaRole);
            userRepository.save(u);
        });
        userRepository.findByUsername("carol.white").ifPresent(u -> {
            u.getRoles().add(designerRole);
            userRepository.save(u);
        });
        log.info("Assigned roles to existing users.");
    }

    // -------------------------------------------------------------------------
    // Categories
    // -------------------------------------------------------------------------
    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already seeded — skipping.");
            return;
        }

        String[][] data = {
            {"Development",  "Software development tasks"},
            {"Design",       "UI/UX design tasks"},
            {"Testing",      "QA and testing activities"},
            {"DevOps",       "Infrastructure and CI/CD"},
            {"Management",   "Project management and planning"},
        };

        for (String[] d : data) {
            Category c = new Category();
            c.setName(d[0]);
            c.setDescription(d[1]);
            categoryRepository.save(c);
        }
        log.info("Seeded {} categories.", data.length);
    }

    // -------------------------------------------------------------------------
    // Users  (creates missing users; migrates plain-text passwords to BCrypt)
    // -------------------------------------------------------------------------
    private List<User> seedUsers() {
        upsertUser("admin",        "admin@taskmagnet.com",  "Admin@12345",  "Admin", "User");
        upsertUser("alice.johnson", "alice@taskmagnet.com",  "Alice@12345",  "Alice", "Johnson",
                   "Engineering",  "Senior Developer");
        upsertUser("bob.smith",    "bob@taskmagnet.com",    "Bob@12345",    "Bob",   "Smith",
                   "Quality Assurance", "QA Engineer");
        upsertUser("carol.white",  "carol@taskmagnet.com",  "Carol@12345",  "Carol", "White",
                   "Design",       "UX Designer");
        log.info("Users seeded/verified.");
        return userRepository.findAll();
    }

    private void upsertUser(String username, String email, String rawPassword,
                            String firstName, String lastName) {
        upsertUser(username, email, rawPassword, firstName, lastName, null, null);
    }

    private void upsertUser(String username, String email, String rawPassword,
                            String firstName, String lastName,
                            String department, String jobTitle) {
        userRepository.findByUsername(username).ifPresentOrElse(
            user -> {
                // Migrate plain-text password to BCrypt if not yet encoded
                if (!user.getPassword().startsWith("$2")) {
                    user.setPassword(passwordEncoder.encode(rawPassword));
                    userRepository.save(user);
                    log.info("Migrated password for user: {}", username);
                }
            },
            () -> {
                User user = new User(username, email,
                        passwordEncoder.encode(rawPassword),
                        firstName, lastName, "system");
                if (department != null) user.setDepartment(department);
                if (jobTitle   != null) user.setJobTitle(jobTitle);
                userRepository.save(user);
                log.info("Created user: {}", username);
            }
        );
    }

    // -------------------------------------------------------------------------
    // Projects
    // -------------------------------------------------------------------------
    private List<Project> seedProjects(List<User> users) {
        if (projectRepository.count() > 0) {
            log.info("Projects already seeded — skipping.");
            return projectRepository.findAll();
        }

        User owner = users.get(0); // admin

        Project p1 = new Project("TaskMagnet Platform", "TMP",
                "Core platform development", owner, "system");
        p1.setStatus(ProjectStatus.ACTIVE);
        p1.setStartDate(LocalDate.now().minusMonths(2));
        p1.setTargetCompletionDate(LocalDate.now().plusMonths(4));
        p1.setColorCode("#1976D2");
        users.forEach(p1.getMembers()::add);

        Project p2 = new Project("Mobile App", "MOB",
                "iOS and Android mobile application", owner, "system");
        p2.setStatus(ProjectStatus.PLANNING);
        p2.setStartDate(LocalDate.now().plusMonths(1));
        p2.setTargetCompletionDate(LocalDate.now().plusMonths(8));
        p2.setColorCode("#388E3C");

        Project p3 = new Project("API Gateway", "APIG",
                "Centralised API gateway and auth service", owner, "system");
        p3.setStatus(ProjectStatus.ACTIVE);
        p3.setStartDate(LocalDate.now().minusMonths(1));
        p3.setTargetCompletionDate(LocalDate.now().plusMonths(3));
        p3.setColorCode("#F57C00");

        List<Project> saved = projectRepository.saveAll(List.of(p1, p2, p3));
        log.info("Seeded {} projects.", saved.size());
        return saved;
    }

    // -------------------------------------------------------------------------
    // Tasks
    // -------------------------------------------------------------------------
    private void seedTasks(List<User> users, List<Project> projects) {
        if (taskRepository.count() > 0) {
            log.info("Tasks already seeded — skipping.");
            return;
        }

        User alice = users.get(1);
        User bob   = users.get(2);
        User carol = users.get(3);
        Project platform = projects.get(0);
        Project mobile   = projects.get(1);
        Project apiGw    = projects.get(2);

        // Platform tasks
        Task t1 = new Task("Design database schema",
                "Design ERD and create Oracle schema scripts", alice, "system");
        t1.setProject(platform);
        t1.setPriority(Priority.HIGH);
        t1.setStatus(com.taskmagnet.enums.TaskStatus.COMPLETED);
        t1.setDueDate(LocalDate.now().minusWeeks(2));
        t1.setProgressPercentage(100);
        t1.markAsCompleted();

        Task t2 = new Task("Implement REST API endpoints",
                "Build CRUD endpoints for all core entities", alice, "system");
        t2.setProject(platform);
        t2.setPriority(Priority.HIGH);
        t2.setStatus(com.taskmagnet.enums.TaskStatus.IN_PROGRESS);
        t2.setDueDate(LocalDate.now().plusWeeks(2));
        t2.setProgressPercentage(60);
        t2.setAssignedTo(alice);

        Task t3 = new Task("Write unit and integration tests",
                "Achieve 80 % code coverage on service layer", bob, "system");
        t3.setProject(platform);
        t3.setPriority(Priority.MEDIUM);
        t3.setStatus(com.taskmagnet.enums.TaskStatus.NOT_STARTED);
        t3.setDueDate(LocalDate.now().plusWeeks(4));
        t3.setAssignedTo(bob);

        Task t4 = new Task("Design UI mockups",
                "Create Figma mockups for the dashboard, task board, and profile", carol, "system");
        t4.setProject(platform);
        t4.setPriority(Priority.MEDIUM);
        t4.setStatus(com.taskmagnet.enums.TaskStatus.IN_PROGRESS);
        t4.setDueDate(LocalDate.now().plusWeeks(1));
        t4.setProgressPercentage(40);
        t4.setAssignedTo(carol);

        // Mobile tasks
        Task t5 = new Task("Setup React Native project",
                "Initialise project, configure navigation and state management", alice, "system");
        t5.setProject(mobile);
        t5.setPriority(Priority.HIGH);
        t5.setStatus(com.taskmagnet.enums.TaskStatus.NOT_STARTED);
        t5.setDueDate(LocalDate.now().plusMonths(2));
        t5.setAssignedTo(alice);

        // API Gateway tasks
        Task t6 = new Task("Implement JWT authentication",
                "Add token-based auth with refresh token support", alice, "system");
        t6.setProject(apiGw);
        t6.setPriority(Priority.CRITICAL);
        t6.setStatus(com.taskmagnet.enums.TaskStatus.IN_PROGRESS);
        t6.setDueDate(LocalDate.now().plusWeeks(3));
        t6.setProgressPercentage(30);
        t6.setAssignedTo(alice);

        Task t7 = new Task("Rate limiting middleware",
                "Add per-user and global rate limiting to all public endpoints", bob, "system");
        t7.setProject(apiGw);
        t7.setPriority(Priority.HIGH);
        t7.setStatus(com.taskmagnet.enums.TaskStatus.NOT_STARTED);
        t7.setDueDate(LocalDate.now().plusWeeks(5));
        t7.setAssignedTo(bob);

        taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7));
        log.info("Seeded 7 tasks.");
    }

    // -------------------------------------------------------------------------
    // Task Statuses
    // -------------------------------------------------------------------------
    private void seedTaskStatuses(List<User> users, List<Project> projects) {
        if (taskStatusRepository.count() > 0) {
            log.info("Task statuses already seeded — skipping.");
            return;
        }

        User admin = users.get(0);
        Project platform = projects.get(0); // TaskMagnet Platform
        Project mobile = projects.get(1);   // Mobile App
        Project apiGw = projects.get(2);    // API Gateway

        // Common task statuses for TaskMagnet Platform
        TaskStatus[] platformStatuses = {
            new TaskStatus("To Do", "Ready to start", "#6c757d", 1, false, platform, admin),
            new TaskStatus("In Progress", "Currently being worked on", "#007bff", 2, false, platform, admin),
            new TaskStatus("Code Review", "Under peer review", "#fd7e14", 3, false, platform, admin),
            new TaskStatus("Testing", "In QA testing phase", "#ffc107", 4, false, platform, admin),
            new TaskStatus("Done", "Completed and deployed", "#28a745", 5, true, platform, admin),
            new TaskStatus("Blocked", "Waiting for external dependency", "#dc3545", 6, false, platform, admin)
        };

        // Agile workflow for Mobile App
        TaskStatus[] mobileStatuses = {
            new TaskStatus("Backlog", "Not yet prioritized", "#6c757d", 1, false, mobile, admin),
            new TaskStatus("Ready", "Ready for development", "#17a2b8", 2, false, mobile, admin),
            new TaskStatus("In Development", "Being developed", "#007bff", 3, false, mobile, admin),
            new TaskStatus("Review", "Code and design review", "#fd7e14", 4, false, mobile, admin),
            new TaskStatus("Staging", "Deployed to staging", "#20c997", 5, false, mobile, admin),
            new TaskStatus("Released", "Live in production", "#28a745", 6, true, mobile, admin)
        };

        // DevOps workflow for API Gateway
        TaskStatus[] apiGwStatuses = {
            new TaskStatus("Planning", "Requirements gathering", "#6f42c1", 1, false, apiGw, admin),
            new TaskStatus("Development", "Implementation phase", "#007bff", 2, false, apiGw, admin),
            new TaskStatus("Security Review", "Security assessment", "#dc3545", 3, false, apiGw, admin),
            new TaskStatus("Performance Test", "Load and performance testing", "#ffc107", 4, false, apiGw, admin),
            new TaskStatus("Production", "Deployed to production", "#28a745", 5, true, apiGw, admin),
            new TaskStatus("Maintenance", "Ongoing maintenance", "#6c757d", 6, false, apiGw, admin)
        };

        // Save all task statuses
        taskStatusRepository.saveAll(List.of(platformStatuses));
        taskStatusRepository.saveAll(List.of(mobileStatuses));
        taskStatusRepository.saveAll(List.of(apiGwStatuses));
        
        int totalCount = platformStatuses.length + mobileStatuses.length + apiGwStatuses.length;
        log.info("Seeded {} task statuses across {} projects.", totalCount, projects.size());
    }
}
