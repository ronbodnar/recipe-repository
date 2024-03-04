package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.model.recipe.Ingredient;
import com.ronbodnar.reciperepository.model.recipe.MeasurementType;
import com.ronbodnar.reciperepository.model.recipe.Recipe;
import com.ronbodnar.reciperepository.model.recipe.RecipeIngredient;
import com.ronbodnar.reciperepository.repository.recipe.RecipeIngredientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    public void save(RecipeIngredient recipeIngredient) {
        this.recipeIngredientRepository.save(recipeIngredient);
    }

    public void saveAll(Iterable<RecipeIngredient> recipeIngredients) {
        this.recipeIngredientRepository.saveAll(recipeIngredients);
    }

    public List<RecipeIngredient> createAll(Recipe recipe, List<RecipeIngredient> recipeIngredientList) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        recipeIngredientList.forEach(recipeIngredientData -> {
            Ingredient ingredient = recipeIngredientData.getIngredient();
            double quantity = recipeIngredientData.getQuantity();
            MeasurementType measurementType = recipeIngredientData.getMeasurementType();

            RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, quantity, measurementType);
            //TODO: add base/core ingredient

            ingredients.add(recipeIngredient);
        });
        return ingredients;
    }
}
