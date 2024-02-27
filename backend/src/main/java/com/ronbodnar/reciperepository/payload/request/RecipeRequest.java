package com.ronbodnar.reciperepository.payload.request;

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

    private List<?> imageData;

    private List<?> ingredients;

    private List<?> instructions;
}
