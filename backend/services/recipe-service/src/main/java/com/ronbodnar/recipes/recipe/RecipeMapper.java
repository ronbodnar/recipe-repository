package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.recipe.dto.RecipeRequest;

import java.util.UUID;

public class RecipeMapper {

    public static Recipe toEntity(RecipeRequest recipeRequest, String authorId) {
        Recipe recipe = new Recipe(recipeRequest);
        recipe.setAuthorId(authorId);

        if (recipeRequest.variants() != null) {
            recipeRequest.variants().forEach(variantRequest -> {
                RecipeVariant variant = new RecipeVariant(variantRequest);
                recipe.addVariant(variant);
            });
        }

        return recipe;
    }
}
