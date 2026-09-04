package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.Recipe;
import com.ronbodnar.recipes.recipe.domain.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public record RecipeDetailsDTO(UUID id,
                               String title,
                               String description,
                               RecipeVisibility visibility,
                               String authorSubject,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               List<UUID> images,
                               List<RecipeVariantDTO> variants,
                               Set<Cuisine> cuisines,
                               Set<MealType> mealTypes,
                               Set<Course> courses,
                               Set<DietType> dietTypes) {

    public static RecipeDetailsDTO fromRecipe(Recipe recipe) {
        return new RecipeDetailsDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getVisibility(),
                recipe.getAuthorSubject(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt(),
                List.copyOf(recipe.getImageIds()),
                recipe.getVariants().stream()
                        .map(RecipeVariantDTO::fromEntity)
                        .toList(),
                Set.copyOf(recipe.getCuisines()),
                Set.copyOf(recipe.getMealTypes()),
                Set.copyOf(recipe.getCourses()),
                Set.copyOf(recipe.getDietTypes())
        );
    }
}
