package com.ronbodnar.reciperepository.nutrient;

import com.ronbodnar.reciperepository.nutrient.NutrientRepository;
import org.springframework.stereotype.Service;

@Service
public class NutrientService {

    private final NutrientRepository nutrientRepository;

    public NutrientService(NutrientRepository nutrientRepository) {
        this.nutrientRepository = nutrientRepository;
    }
}
