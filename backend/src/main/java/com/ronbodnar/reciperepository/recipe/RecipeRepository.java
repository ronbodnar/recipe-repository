package com.ronbodnar.reciperepository.recipe;

import com.ronbodnar.reciperepository.recipe.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> { }