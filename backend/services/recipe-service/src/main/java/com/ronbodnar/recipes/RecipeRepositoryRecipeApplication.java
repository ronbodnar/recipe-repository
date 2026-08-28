package com.ronbodnar.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RecipeRepositoryRecipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryRecipeApplication.class, args);
    }
}