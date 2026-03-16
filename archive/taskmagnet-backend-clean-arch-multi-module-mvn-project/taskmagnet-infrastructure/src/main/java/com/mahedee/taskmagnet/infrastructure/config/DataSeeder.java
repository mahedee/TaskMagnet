package com.mahedee.taskmagnet.infrastructure.config;

import com.mahedee.taskmagnet.domain.model.User;
import com.mahedee.taskmagnet.domain.model.auth.ERole;
import com.mahedee.taskmagnet.domain.model.auth.Role;
import com.mahedee.taskmagnet.domain.repository.RoleRepository;
import com.mahedee.taskmagnet.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(role));
                logger.info("Seeded role: {}", role);
            }
        }
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@taskmagnet.com",
                    passwordEncoder.encode("Admin@123"));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            logger.info("Seeded admin user");
        }
    }
}
