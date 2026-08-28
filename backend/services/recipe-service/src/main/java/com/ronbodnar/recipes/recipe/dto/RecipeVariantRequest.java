package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.domain.CookingMethod;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeVariantRequest(
        String name,
        int prepTime,
        int cookTime,
        int servings,
        @NotNull CookingMethod cookingMethod,
        List<String> ingredients,
        List<String> instructions,
        List<String> notes
) {

    public RecipeVariantRequest {
        ingredients = ingredients == null ? List.of() : ingredients;
        instructions = instructions == null ? List.of() : instructions;
        notes = notes == null ? List.of() : notes;
    }
}
