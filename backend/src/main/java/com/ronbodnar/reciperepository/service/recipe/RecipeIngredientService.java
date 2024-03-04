package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.model.recipe.Ingredient;
import com.ronbodnar.reciperepository.model.recipe.MeasurementType;
import com.ronbodnar.reciperepository.model.recipe.Recipe;
import com.ronbodnar.reciperepository.model.recipe.RecipeIngredient;
import com.ronbodnar.reciperepository.repository.recipe.IngredientRepository;
import com.ronbodnar.reciperepository.repository.recipe.MeasurementTypeRepository;
import com.ronbodnar.reciperepository.repository.recipe.RecipeIngredientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeIngredientService {

    private final IngredientRepository ingredientRepository;

    private final MeasurementTypeRepository measurementTypeRepository;

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredientService(IngredientRepository ingredientRepository, MeasurementTypeRepository measurementTypeRepository, RecipeIngredientRepository recipeIngredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.measurementTypeRepository = measurementTypeRepository;
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
            double quantity = recipeIngredientData.getQuantity();
            MeasurementType.Kind measurementKind = recipeIngredientData.getMeasurementType().getKind();

            String ingredientName = recipeIngredientData.getIngredient().getName();

            Optional<Ingredient> ingredient = ingredientRepository.findByName(ingredientName);

            //TODO: exception handling to notify of invalid ingredient/measurement type on front-end
            if (ingredient.isPresent()) {
                Optional<MeasurementType> measurementType = measurementTypeRepository.findByKind(measurementKind);

                if (measurementType.isPresent()) {
                    RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient.get(), quantity, measurementType.get());

                    ingredients.add(recipeIngredient);
                }
            }
        });
        return ingredients;
    }
}
