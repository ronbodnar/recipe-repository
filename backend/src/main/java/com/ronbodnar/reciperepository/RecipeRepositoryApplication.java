package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.model.Role;
import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.RoleRepository;
import com.ronbodnar.reciperepository.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class RecipeRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            roleRepository.save(new Role(0, "ROLE_USER"));
            roleRepository.save(new Role(0, "ROLE_PREMIUM"));
            roleRepository.save(new Role(0, "ROLE_ADMIN"));

            Set<Role> roles = new HashSet<>();

            Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
            userRole.ifPresent(roles::add);

            Optional<Role> premiumRole = roleRepository.findByName("ROLE_PREMIUM");
            premiumRole.ifPresent(roles::add);

            String password = new BCryptPasswordEncoder().encode("password");

            User user = new User("user", "John", "Doe", "jodoe@outlook.com", password);
            user.setRoles(roles);
            userRepository.save(user);
        };
    }
}