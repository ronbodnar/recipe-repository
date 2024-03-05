package com.ronbodnar.reciperepository.nutritionfact;

import com.ronbodnar.reciperepository.nutritionfact.NutritionFactRepository;
import org.springframework.stereotype.Service;

@Service
public class NutritionFactService {

    private final NutritionFactRepository nutritionFactRepository;

    public NutritionFactService(NutritionFactRepository nutritionFactRepository) {
        this.nutritionFactRepository = nutritionFactRepository;
    }
}
