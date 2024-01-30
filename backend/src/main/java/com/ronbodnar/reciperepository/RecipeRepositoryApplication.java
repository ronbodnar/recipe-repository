package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecipeRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            User user1 = new User("test", "JoDoe", "John", "Doe", "jodoe@outlook.com", "hi");
            userRepository.save(user1);

            User user2 = new User("test1", "JaDoe", "Jane", "Doe", "jadoe@outlook.com", "hi");
            userRepository.save(user2);

            System.out.println("Added users to repo:");
            userRepository.findAll().forEach(System.out::println);
        };
    }
}