package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.NutrientRepository;
import org.springframework.stereotype.Service;

@Service
public class NutrientService {

    private final NutrientRepository nutrientRepository;

    public NutrientService(NutrientRepository nutrientRepository) {
        this.nutrientRepository = nutrientRepository;
    }
}
