package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.PreparationTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class PreparationTypeService {

    private final PreparationTypeRepository preparationTypeRepository;

    public PreparationTypeService(PreparationTypeRepository preparationTypeRepository) {
        this.preparationTypeRepository = preparationTypeRepository;
    }
}
