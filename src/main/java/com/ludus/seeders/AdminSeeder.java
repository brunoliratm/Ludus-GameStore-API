package com.ludus.seeders;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.ludus.enums.UserRole;
import com.ludus.infra.config.AdminConfig;
import com.ludus.models.UserModel;
import com.ludus.repositories.UserRepository;
import com.ludus.services.UserService;
import org.slf4j.Logger;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private final AdminConfig adminConfig;
    private final UserRepository userRepository;

    public AdminSeeder(UserService userService, AdminConfig adminConfig, UserRepository userRepository) {
        this.userService = userService;
        this.adminConfig = adminConfig;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userService.findByEmail(adminConfig.getEmail()).isEmpty()) {
            UserModel admin = new UserModel();

            admin.setName("Admin");
            admin.setEmail(adminConfig.getEmail());
            admin.setPassword(new BCryptPasswordEncoder().encode(adminConfig.getPassword()));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            log.info("ADMIN user created");
        }
    }
}