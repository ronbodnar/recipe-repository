package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.recipe.dto.RecipeRequest;

public class RecipeMapper {

    public static Recipe toEntity(RecipeRequest recipeRequest, String authorSubject) {
        Recipe recipe = new Recipe(recipeRequest);
        recipe.setAuthorSubject(authorSubject);

        if (recipeRequest.variants() != null) {
            recipeRequest.variants().forEach(variantRequest -> {
                RecipeVariant variant = new RecipeVariant(variantRequest);
                recipe.addVariant(variant);
            });
        }

        return recipe;
    }
}
