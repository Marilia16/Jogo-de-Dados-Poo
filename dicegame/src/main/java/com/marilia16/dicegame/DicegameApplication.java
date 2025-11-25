package com.marilia16.dicegame;

import com.marilia16.dicegame.model.User;
import com.marilia16.dicegame.repository.UserRepository;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DicegameApplication {

    public static void main(String[] args) {
        SpringApplication.run(DicegameApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            String adminEmail = "marigabi916@gmail.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("ADMIN");
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode("mari4010L"));
                admin.setRole("ADMIN");
                admin.setValueTotal(new BigDecimal(0));

                userRepository.save(admin);
                System.out.println("Admin criado: " + adminEmail);
            } else {
                System.out.println("Admin já existe: " + adminEmail);
            }
        };
    }
}
