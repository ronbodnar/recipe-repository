package com.ronbodnar.reciperepository.payload.request;

import com.ronbodnar.reciperepository.model.recipe.Instruction;
import com.ronbodnar.reciperepository.model.recipe.RecipeImage;
import com.ronbodnar.reciperepository.model.recipe.RecipeIngredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    private String title;

    private String description;

    private int prepTime, cookTime;

    private int servings;

    private List<RecipeImage> imageData;

    private List<RecipeIngredient> ingredients;

    private List<Instruction> instructions;
}
