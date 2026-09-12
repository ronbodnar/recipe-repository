package com.ronbodnar.recipes;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.identity.role.Role;
import com.ronbodnar.recipes.modules.identity.role.RoleRepository;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class RecipeRepositoryRecipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryRecipeApplication.class, args);
    }

    @Value("${app.security.default-admin-password}")
    private String defaultAdminPassword;

    @Bean
    @Profile("!prod")
    CommandLineRunner init(RoleRepository roleRepository, UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userAccountRepository.existsByUsername("admin")) {
                Role userRole = roleRepository.findByName("USER")
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.ROLE_NOT_FOUND,
                                        "Failed to find Administrator role during initialization"
                                )
                        );

                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
                admin.setEmail("admin@email.com");
                admin.setRoles(Set.of(userRole));
                userAccountRepository.save(admin);
            }
        };
    }
}