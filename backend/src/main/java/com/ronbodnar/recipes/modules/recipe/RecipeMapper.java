package com.ronbodnar.recipes.modules.recipe;

import com.ronbodnar.recipes.modules.recipe.dto.RecipeRequest;

public class RecipeMapper {

    public static Recipe toEntity(RecipeRequest recipeRequest, Long authorId) {
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
