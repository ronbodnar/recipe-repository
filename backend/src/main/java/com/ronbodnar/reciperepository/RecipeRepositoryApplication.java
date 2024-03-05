package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.attribute.Attribute;
import com.ronbodnar.reciperepository.attribute.AttributeRepository;
import com.ronbodnar.reciperepository.cuisine.Cuisine;
import com.ronbodnar.reciperepository.cuisine.CuisineRepository;
import com.ronbodnar.reciperepository.fooditem.FoodItem;
import com.ronbodnar.reciperepository.fooditem.FoodItemRepository;
import com.ronbodnar.reciperepository.mealtype.MealType;
import com.ronbodnar.reciperepository.mealtype.MealTypeRepository;
import com.ronbodnar.reciperepository.measurementtype.MeasurementType;
import com.ronbodnar.reciperepository.measurementtype.MeasurementTypeRepository;
import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import com.ronbodnar.reciperepository.preparationtype.PreparationTypeRepository;
import com.ronbodnar.reciperepository.role.Role;
import com.ronbodnar.reciperepository.role.RoleRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class RecipeRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeRepositoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FoodItemRepository foodItemRepository, MeasurementTypeRepository measurementTypeRepository, PreparationTypeRepository preparationTypeRepository, RoleRepository roleRepository, MealTypeRepository mealTypeRepository, CuisineRepository cuisineRepository, AttributeRepository attributeRepository) {
        return args -> {
            boolean addData = true;

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

                // Add MeasurementTypes
                List<MeasurementType.Kind> measurementTypes = List.of(MeasurementType.Kind.values());
                measurementTypes.forEach(measurementType -> measurementTypeRepository.save(new MeasurementType(measurementType)));

                // Add PreparationTypes
                List<PreparationType.Kind> preparationTypes = List.of(PreparationType.Kind.values());
                preparationTypes.forEach(preparationType -> preparationTypeRepository.save(new PreparationType(preparationType)));

                // Add temp ingredients
                foodItemRepository.save(new FoodItem("Chicken breast", new HashSet<>()));
                foodItemRepository.save(new FoodItem("Tomato sauce", new HashSet<>()));
            }
        };
    }
}