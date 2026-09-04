package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.RecipeVariant;
import com.ronbodnar.recipes.recipe.domain.CookingMethod;

import java.util.List;
import java.util.UUID;

public record RecipeVariantDTO(UUID id,
                               String name,
                               Integer prepTime,
                               Integer cookTime,
                               String yield,
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
