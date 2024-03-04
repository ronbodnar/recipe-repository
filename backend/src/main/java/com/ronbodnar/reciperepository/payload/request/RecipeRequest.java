package com.ronbodnar.reciperepository.payload.request;

import com.ronbodnar.reciperepository.model.recipe.*;
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

    private List<ImageData> imageData;

    private List<RecipeIngredient> ingredients;

    private List<Instruction> instructions;

    private List<Course> courses;

    private List<Cuisine> cuisines;

    private List<Attribute> attributes;
}
