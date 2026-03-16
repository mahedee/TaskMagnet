package com.taskmagnet.config;

import com.taskmagnet.entity.Category;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.Role;
import com.taskmagnet.entity.Task;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.Priority;
import com.taskmagnet.enums.ProjectStatus;
import com.taskmagnet.enums.RoleName;
import com.taskmagnet.enums.TaskStatus;
import com.taskmagnet.repository.CategoryRepository;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.RoleRepository;
import com.taskmagnet.repository.TaskRepository;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository,
                      UserRepository userRepository,
                      ProjectRepository projectRepository,
                      TaskRepository taskRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
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
        t1.setStatus(TaskStatus.COMPLETED);
        t1.setDueDate(LocalDate.now().minusWeeks(2));
        t1.setProgressPercentage(100);
        t1.markAsCompleted();

        Task t2 = new Task("Implement REST API endpoints",
                "Build CRUD endpoints for all core entities", alice, "system");
        t2.setProject(platform);
        t2.setPriority(Priority.HIGH);
        t2.setStatus(TaskStatus.IN_PROGRESS);
        t2.setDueDate(LocalDate.now().plusWeeks(2));
        t2.setProgressPercentage(60);
        t2.setAssignedTo(alice);

        Task t3 = new Task("Write unit and integration tests",
                "Achieve 80 % code coverage on service layer", bob, "system");
        t3.setProject(platform);
        t3.setPriority(Priority.MEDIUM);
        t3.setStatus(TaskStatus.NOT_STARTED);
        t3.setDueDate(LocalDate.now().plusWeeks(4));
        t3.setAssignedTo(bob);

        Task t4 = new Task("Design UI mockups",
                "Create Figma mockups for the dashboard, task board, and profile", carol, "system");
        t4.setProject(platform);
        t4.setPriority(Priority.MEDIUM);
        t4.setStatus(TaskStatus.IN_PROGRESS);
        t4.setDueDate(LocalDate.now().plusWeeks(1));
        t4.setProgressPercentage(40);
        t4.setAssignedTo(carol);

        // Mobile tasks
        Task t5 = new Task("Setup React Native project",
                "Initialise project, configure navigation and state management", alice, "system");
        t5.setProject(mobile);
        t5.setPriority(Priority.HIGH);
        t5.setStatus(TaskStatus.NOT_STARTED);
        t5.setDueDate(LocalDate.now().plusMonths(2));
        t5.setAssignedTo(alice);

        // API Gateway tasks
        Task t6 = new Task("Implement JWT authentication",
                "Add token-based auth with refresh token support", alice, "system");
        t6.setProject(apiGw);
        t6.setPriority(Priority.CRITICAL);
        t6.setStatus(TaskStatus.IN_PROGRESS);
        t6.setDueDate(LocalDate.now().plusWeeks(3));
        t6.setProgressPercentage(30);
        t6.setAssignedTo(alice);

        Task t7 = new Task("Rate limiting middleware",
                "Add per-user and global rate limiting to all public endpoints", bob, "system");
        t7.setProject(apiGw);
        t7.setPriority(Priority.HIGH);
        t7.setStatus(TaskStatus.NOT_STARTED);
        t7.setDueDate(LocalDate.now().plusWeeks(5));
        t7.setAssignedTo(bob);

        taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6, t7));
        log.info("Seeded 7 tasks.");
    }
}
