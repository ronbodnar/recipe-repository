package com.ronbodnar.reciperepository.ingredient;

import com.ronbodnar.reciperepository.fooditem.FoodItem;
import com.ronbodnar.reciperepository.measurementtype.MeasurementType;
import com.ronbodnar.reciperepository.recipe.Recipe;
import com.ronbodnar.reciperepository.ingredient.Ingredient;
import com.ronbodnar.reciperepository.fooditem.FoodItemRepository;
import com.ronbodnar.reciperepository.measurementtype.MeasurementTypeRepository;
import com.ronbodnar.reciperepository.ingredient.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final FoodItemRepository foodItemRepository;

    private final MeasurementTypeRepository measurementTypeRepository;

    private final IngredientRepository ingredientRepository;

    public IngredientService(FoodItemRepository foodItemRepository, MeasurementTypeRepository measurementTypeRepository, IngredientRepository ingredientRepository) {
        this.foodItemRepository = foodItemRepository;
        this.measurementTypeRepository = measurementTypeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public void save(Ingredient ingredient) {
        this.ingredientRepository.save(ingredient);
    }

    public void saveAll(Iterable<Ingredient> recipeIngredients) {
        this.ingredientRepository.saveAll(recipeIngredients);
    }

    public List<Ingredient> createAll(Recipe recipe, List<Ingredient> ingredientList) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientList.forEach(recipeIngredientData -> {
            double quantity = recipeIngredientData.getQuantity();
            MeasurementType.Kind measurementKind = recipeIngredientData.getMeasurementType().getKind();

            String ingredientName = recipeIngredientData.getFoodItem().getName();

            Optional<FoodItem> ingredient = foodItemRepository.findByName(ingredientName);

            //TODO: exception handling to notify of invalid ingredient/measurement type on front-end
            if (ingredient.isPresent()) {
                Optional<MeasurementType> measurementType = measurementTypeRepository.findByKind(measurementKind);

                if (measurementType.isPresent()) {
                    Ingredient recipeIngredient = new Ingredient(recipe, ingredient.get(), quantity, measurementType.get());

                    ingredients.add(recipeIngredient);
                }
            }
        });
        return ingredients;
    }
}
