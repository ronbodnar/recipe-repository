package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.RecipeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient, Integer> { }