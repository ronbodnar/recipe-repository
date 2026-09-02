package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.domain.MealType;
import com.ronbodnar.recipes.recipe.domain.Cuisine;
import com.ronbodnar.recipes.recipe.domain.DietType;
import com.ronbodnar.recipes.recipe.domain.Course;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RecipeRequest(
        UUID id,
        @NotBlank String title,
        String description,
        List<UUID> imageIds,
        List<RecipeVariantRequest> variants,
        Set<Course> courses,
        Set<Cuisine> cuisines,
        Set<MealType> mealTypes,
        Set<DietType> dietTypes
) {

    public RecipeRequest {
        imageIds = imageIds == null ? List.of() : imageIds;
        variants = variants == null ? List.of() : variants;
        cuisines = cuisines == null ? Set.of() : cuisines;
        mealTypes = mealTypes == null ? Set.of() : mealTypes;
        courses = courses == null ? Set.of() : courses;
        dietTypes = dietTypes == null ? Set.of() : dietTypes;
    }

    // Needed to simplify testing setup
    public RecipeRequest(UUID id, String title, String description, List<RecipeVariantRequest> variants) {
        this(id, title, description, List.of(), variants, null, null, null, null);
    }
}