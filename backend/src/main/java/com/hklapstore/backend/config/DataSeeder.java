package com.hklapstore.backend.config;

import com.hklapstore.backend.entity.User;
import com.hklapstore.backend.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDefaultUser(UserRepo userRepo) {
        return args -> {
            if (userRepo.findByUsername("admin") == null) {
                var encoder = new BCryptPasswordEncoder(12);
                var user = new User();
                user.setUsername("admin");
                user.setPassword(encoder.encode("admin123"));
                userRepo.save(user);
                System.out.println("[Seeder] Seeded default admin user (username=admin, password=admin123)");
            }
        };
    }
}
