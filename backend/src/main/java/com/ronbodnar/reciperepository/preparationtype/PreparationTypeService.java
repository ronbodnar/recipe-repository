package com.ronbodnar.reciperepository.preparationtype;

import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import com.ronbodnar.reciperepository.recipe.Recipe;
import com.ronbodnar.reciperepository.preparationtype.PreparationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PreparationTypeService {

    private final PreparationTypeRepository preparationTypeRepository;

    public PreparationTypeService(PreparationTypeRepository preparationTypeRepository) {
        this.preparationTypeRepository = preparationTypeRepository;
    }

    public Set<PreparationType> createAll(Recipe recipe, Set<PreparationType> preparationTypeSet) {
        return null;
    }
}
