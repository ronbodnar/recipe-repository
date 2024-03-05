package com.ronbodnar.reciperepository.ingredient;

import com.ronbodnar.reciperepository.ingredient.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> { }