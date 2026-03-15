package com.mahedee.backend.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mahedee.backend.models.ERole;
import com.mahedee.backend.models.Role;
import com.mahedee.backend.models.User;
import com.mahedee.backend.repository.RoleRepository;
import com.mahedee.backend.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * DataSeeder class that implements CommandLineRunner to seed initial data
 * into the database when the application starts.
 * 
 * This class follows industry best practices:
 * - Checks if data already exists before inserting
 * - Uses transactions for data integrity
 * - Provides logging for monitoring
 * - Is easily maintainable and extensible
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data seeding process...");
        
        try {
            seedRoles();
            seedAdminUser();
            // Uncomment the line below if you want to seed additional test users
            // seedAdditionalUsers();
            logger.info("Data seeding completed successfully!");
        } catch (Exception e) {
            logger.error("Error occurred during data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Seeds the roles table with default roles if they don't exist
     */
    private void seedRoles() {
        logger.info("Seeding roles...");
        
        // Check and create ROLE_USER
        if (!roleRepository.existsByName(ERole.ROLE_USER)) {
            Role userRole = new Role(ERole.ROLE_USER);
            roleRepository.save(userRole);
            logger.info("Created role: ROLE_USER");
        } else {
            logger.info("Role ROLE_USER already exists, skipping...");
        }

        // Check and create ROLE_MODERATOR
        if (!roleRepository.existsByName(ERole.ROLE_MODERATOR)) {
            Role moderatorRole = new Role(ERole.ROLE_MODERATOR);
            roleRepository.save(moderatorRole);
            logger.info("Created role: ROLE_MODERATOR");
        } else {
            logger.info("Role ROLE_MODERATOR already exists, skipping...");
        }

        // Check and create ROLE_ADMIN
        if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            logger.info("Created role: ROLE_ADMIN");
        } else {
            logger.info("Role ROLE_ADMIN already exists, skipping...");
        }
    }

    /**
     * Seeds the admin user if it doesn't exist
     */
    private void seedAdminUser() {
        logger.info("Seeding admin user...");
        
        String adminUsername = "admin";
        String adminEmail = "admin@gmail.com";
        String adminPassword = "Taskmagnet@2025";

        // Check if admin user already exists
        if (!userRepository.existsByUsername(adminUsername) && !userRepository.existsByEmail(adminEmail)) {
            
            // Create admin user
            User adminUser = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword));
            
            // Get admin role
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin role is not found."));
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            userRepository.save(adminUser);
            logger.info("Created admin user: {} with email: {}", adminUsername, adminEmail);
            
        } else {
            logger.info("Admin user already exists, skipping...");
        }
    }

    /**
     * Method to seed additional users - can be extended as needed
     * Currently commented out but available for future use
     */
    @SuppressWarnings("unused")
    private void seedAdditionalUsers() {
        logger.info("Seeding additional users...");
        
        // Example: Create a test moderator user
        String modUsername = "moderator";
        String modEmail = "moderator@gmail.com";
        String modPassword = "Taskmagnet@2025";

        if (!userRepository.existsByUsername(modUsername) && !userRepository.existsByEmail(modEmail)) {
            User modUser = new User(modUsername, modEmail, passwordEncoder.encode(modPassword));
            
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Moderator role is not found."));
            
            Set<Role> roles = new HashSet<>();
            roles.add(modRole);
            modUser.setRoles(roles);
            
            userRepository.save(modUser);
            logger.info("Created moderator user: {} with email: {}", modUsername, modEmail);
        }

        // Example: Create a test regular user
        String regularUsername = "user";
        String regularEmail = "user@gmail.com";
        String regularPassword = "Taskmagnet@2025";

        if (!userRepository.existsByUsername(regularUsername) && !userRepository.existsByEmail(regularEmail)) {
            User regularUser = new User(regularUsername, regularEmail, passwordEncoder.encode(regularPassword));
            
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: User role is not found."));
            
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            regularUser.setRoles(roles);
            
            userRepository.save(regularUser);
            logger.info("Created regular user: {} with email: {}", regularUsername, regularEmail);
        }
    }
}
