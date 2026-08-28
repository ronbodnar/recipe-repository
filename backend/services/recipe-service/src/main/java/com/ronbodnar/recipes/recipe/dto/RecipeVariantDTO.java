package com.ronbodnar.recipes.recipe.dto;

import com.ronbodnar.recipes.recipe.RecipeVariant;
import com.ronbodnar.recipes.recipe.domain.CookingMethod;

import java.util.List;
import java.util.UUID;

public record RecipeVariantDTO(UUID id,
                               String name,
                               Integer prepTime,
                               Integer cookTime,
                               Integer servings,
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
                recipeVariant.getServings(),
                recipeVariant.getCookingMethod(),
                recipeVariant.getIngredients(),
                recipeVariant.getInstructions(),
                recipeVariant.getNotes()
        );
    }

}
