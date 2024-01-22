package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.repository.AccountRepository;
import com.ronbodnar.reciperepository.model.Account;
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
    CommandLineRunner init(AccountRepository userRepository) {
        return args -> {
            Account account1 = new Account("test", "JoDoe", "John", "Doe", "jodoe@outlook.com", "");
            userRepository.save(account1);

            Account account2 = new Account("test1", "JaDoe", "Jane", "Doe", "jadoe@outlook.com", "");
            userRepository.save(account2);

            System.out.println("Added accounts to repo:");
            userRepository.findAll().forEach(System.out::println);
        };
    }
}