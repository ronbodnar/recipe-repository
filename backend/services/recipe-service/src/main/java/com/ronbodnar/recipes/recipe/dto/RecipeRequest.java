package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.domain.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RecipeRequest(
        UUID id,
        @NotBlank @Size(max = 50) String title,
        String description,
        RecipeVisibility visibility,
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
    public RecipeRequest(UUID id, String title, String description, RecipeVisibility visibility, List<RecipeVariantRequest> variants) {
        this(id, title, description, visibility, List.of(), variants, null, null, null, null);
    }
}