package com.ronbodnar.reciperepository;

import com.ronbodnar.reciperepository.model.recipe.Attribute;
import com.ronbodnar.reciperepository.model.recipe.Course;
import com.ronbodnar.reciperepository.model.recipe.Cuisine;
import com.ronbodnar.reciperepository.model.user.Role;
import com.ronbodnar.reciperepository.repository.AttributeRepository;
import com.ronbodnar.reciperepository.repository.CourseRepository;
import com.ronbodnar.reciperepository.repository.CuisineRepository;
import com.ronbodnar.reciperepository.repository.RoleRepository;
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
    CommandLineRunner init(RoleRepository roleRepository, CourseRepository courseRepository, CuisineRepository cuisineRepository, AttributeRepository attributeRepository) {
        return args -> {
            boolean addData = false;

            if (addData) {
                // Add Roles
                List<Role.RoleType> roles = List.of(Role.RoleType.values());
                roles.forEach(role -> roleRepository.save(new Role(role)));

                // Add Courses
                List<Course.CourseType> courses = List.of(Course.CourseType.values());
                courses.forEach(course -> courseRepository.save(new Course(course)));

                // Add Cuisines
                List<Cuisine.CuisineType> cuisines = List.of(Cuisine.CuisineType.values());
                cuisines.forEach(cuisine -> cuisineRepository.save(new Cuisine(cuisine)));

                // Add Attributes
                List<Attribute.AttributeType> attributes = List.of(Attribute.AttributeType.values());
                attributes.forEach(attribute -> attributeRepository.save(new Attribute(attribute)));
            }
        };
    }
}