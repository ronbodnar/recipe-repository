package com.ronbodnar.reciperepository.mealtype;

import com.ronbodnar.reciperepository.mealtype.MealType;
import com.ronbodnar.reciperepository.recipe.Recipe;
import com.ronbodnar.reciperepository.mealtype.MealTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MealTypeService {

    private final MealTypeRepository mealTypeRepository;

    public MealTypeService(MealTypeRepository mealTypeRepository) {
        this.mealTypeRepository = mealTypeRepository;
    }

    public Set<MealType> createAll(Recipe recipe, Set<MealType> mealTypeList) {
        return null;
    }
}
