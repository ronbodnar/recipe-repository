package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.model.recipe.Attribute;
import com.ronbodnar.reciperepository.model.recipe.MealType;
import com.ronbodnar.reciperepository.model.recipe.Cuisine;
import com.ronbodnar.reciperepository.model.user.Role;
import com.ronbodnar.reciperepository.repository.recipe.AttributeRepository;
import com.ronbodnar.reciperepository.repository.recipe.MealTypeRepository;
import com.ronbodnar.reciperepository.repository.recipe.CuisineRepository;
import com.ronbodnar.reciperepository.repository.user.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class RecipeRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepository, MealTypeRepository mealTypeRepository, CuisineRepository cuisineRepository, AttributeRepository attributeRepository) {
        return args -> {
            boolean addData = false;

            if (addData) {
                // Add Roles
                List<Role.RoleType> roles = List.of(Role.RoleType.values());
                roles.forEach(role -> roleRepository.save(new Role(role)));

                // Add Meals
                List<MealType.Kind> meals = List.of(MealType.Kind.values());
                meals.forEach(meal -> mealTypeRepository.save(new MealType(meal)));

                // Add Cuisines
                List<Cuisine.Kind> cuisines = List.of(Cuisine.Kind.values());
                cuisines.forEach(cuisine -> cuisineRepository.save(new Cuisine(cuisine)));

                // Add Attributes
                List<Attribute.Kind> attributes = List.of(Attribute.Kind.values());
                attributes.forEach(attribute -> attributeRepository.save(new Attribute(attribute)));
            }
        };
    }
}