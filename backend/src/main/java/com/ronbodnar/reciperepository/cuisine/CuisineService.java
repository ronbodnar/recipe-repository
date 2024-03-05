package com.ronbodnar.reciperepository.cuisine;

import com.ronbodnar.reciperepository.cuisine.Cuisine;
import com.ronbodnar.reciperepository.recipe.Recipe;
import com.ronbodnar.reciperepository.cuisine.CuisineRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CuisineService {

    private final CuisineRepository cuisineRepository;

    public CuisineService(CuisineRepository cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }

    public Set<Cuisine> createAll(Recipe recipe, Set<Cuisine> cuisineList) {
        return null;
    }
}
