package com.ronbodnar.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RecipeRepositoryImageApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryImageApplication.class, args);
    }
}