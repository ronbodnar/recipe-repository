package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> { }