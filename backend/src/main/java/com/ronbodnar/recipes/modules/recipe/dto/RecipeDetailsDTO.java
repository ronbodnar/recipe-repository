package com.ronbodnar.recipes.modules.recipe.dto;

import com.ronbodnar.recipes.modules.recipe.Recipe;
import com.ronbodnar.recipes.modules.recipe.domain.*;

import java.time.LocalDateTime;
import java.util.*;

public record RecipeDetailsDTO(UUID id,
                               String title,
                               String description,
                               String source,
                               RecipeVisibility visibility,
                               Long authorId,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               List<UUID> images,
                               List<RecipeVariantDTO> variants,
                               Set<Cuisine> cuisines,
                               Set<MealType> mealTypes,
                               Set<Course> courses,
                               Set<DietType> dietTypes) {

    public static RecipeDetailsDTO from(Recipe recipe) {
        return new RecipeDetailsDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getSource(),
                recipe.getVisibility(),
                recipe.getAuthorId(),
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
