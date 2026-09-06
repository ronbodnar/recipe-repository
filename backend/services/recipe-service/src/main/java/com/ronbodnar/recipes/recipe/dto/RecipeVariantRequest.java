package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.domain.CookingMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeVariantRequest(
        @Size(max = 50) String name,
        @Min(0) @Max(2880) int prepTime,
        @Min(0) @Max(2880) int cookTime,
        @Size(max = 50) String yield,
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
