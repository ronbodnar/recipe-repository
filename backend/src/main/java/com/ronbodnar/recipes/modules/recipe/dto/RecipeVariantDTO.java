package com.ronbodnar.recipes.modules.recipe.dto;

import com.ronbodnar.recipes.modules.recipe.RecipeVariant;
import com.ronbodnar.recipes.modules.recipe.domain.CookingMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record RecipeVariantDTO(UUID id,
                               @Size(max = 50) String name,
                               @Min(0) @Max(2880) Integer prepTime,
                               @Min(0) @Max(2880) Integer cookTime,
                               @Size(max = 50) String yield,
                               CookingMethod cookingMethod,
                               List<String> ingredients,
                               List<String> instructions,
                               List<String> notes) {

    public static RecipeVariantDTO fromEntity(RecipeVariant recipeVariant) {
        return new RecipeVariantDTO(
                recipeVariant.getId(),
                recipeVariant.getName(),
                recipeVariant.getPrepTime(),
                recipeVariant.getCookTime(),
                recipeVariant.getYield(),
                recipeVariant.getCookingMethod(),
                List.copyOf(recipeVariant.getIngredients()),
                List.copyOf(recipeVariant.getInstructions()),
                List.copyOf(recipeVariant.getNotes())
        );
    }

}
