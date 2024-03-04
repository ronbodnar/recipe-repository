package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.NutritionFactRepository;
import org.springframework.stereotype.Service;

@Service
public class NutritionFactService {

    private final NutritionFactRepository nutritionFactRepository;

    public NutritionFactService(NutritionFactRepository nutritionFactRepository) {
        this.nutritionFactRepository = nutritionFactRepository;
    }
}
