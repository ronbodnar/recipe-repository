package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.MealTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class MealTypeService {

    private final MealTypeRepository mealTypeRepository;

    public MealTypeService(MealTypeRepository mealTypeRepository) {
        this.mealTypeRepository = mealTypeRepository;
    }
}
