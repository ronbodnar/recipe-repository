package com.ronbodnar.reciperepository.payload.request;

import com.ronbodnar.reciperepository.attribute.Attribute;
import com.ronbodnar.reciperepository.cuisine.Cuisine;
import com.ronbodnar.reciperepository.imagedata.ImageData;
import com.ronbodnar.reciperepository.ingredient.Ingredient;
import com.ronbodnar.reciperepository.instruction.Instruction;
import com.ronbodnar.reciperepository.mealtype.MealType;
import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    private String title;

    private String description;

    private int prepTime, cookTime;

    private int servings;

    private List<ImageData> imageData;

    private List<Ingredient> ingredients;

    private List<Instruction> instructions;

    private Set<MealType> mealTypes;

    private Set<Cuisine> cuisines;

    private Set<Attribute> attributes;

    private Set<PreparationType> preparationTypes;

    private Set<String> tags;
}
