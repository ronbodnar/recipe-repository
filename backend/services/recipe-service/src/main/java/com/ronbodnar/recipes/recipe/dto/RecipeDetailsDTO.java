package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.Recipe;
import com.ronbodnar.recipes.recipe.domain.MealType;
import com.ronbodnar.recipes.recipe.domain.Cuisine;
import com.ronbodnar.recipes.recipe.domain.DietType;
import com.ronbodnar.recipes.recipe.domain.Course;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record RecipeDetailsDTO(UUID id,
                               String title,
                               String description,
                               UUID authorId,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               List<UUID> images,
                               Set<RecipeVariantDTO> variants,
                               Set<Cuisine> cuisines,
                               Set<MealType> mealTypes,
                               Set<Course> courses,
                               Set<DietType> dietTypes) {

    public static RecipeDetailsDTO fromRecipe(Recipe recipe, List<UUID> imageIds) {
        return new RecipeDetailsDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getAuthorId(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt(),
                imageIds,
                recipe.getVariants().stream()
                        .map(RecipeVariantDTO::fromEntity)
                        .collect(Collectors.toSet()),
                new HashSet<>(recipe.getCuisines()),
                new HashSet<>(recipe.getMealTypes()),
                new HashSet<>(recipe.getCourses()),
                new HashSet<>(recipe.getDietTypes())
        );
    }
}
