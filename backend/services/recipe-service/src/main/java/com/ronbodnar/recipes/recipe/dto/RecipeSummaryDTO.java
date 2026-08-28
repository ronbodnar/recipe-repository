package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.Recipe;

import java.util.List;
import java.util.UUID;

public record RecipeSummaryDTO(UUID id, String title, String description, List<UUID> imageIds) {

    public static RecipeSummaryDTO fromEntity(Recipe recipe, List<UUID> imageIds) {
        return new RecipeSummaryDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                imageIds
        );
    }
}
