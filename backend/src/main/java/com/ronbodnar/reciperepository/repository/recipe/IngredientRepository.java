package com.ronbodnar.reciperepository.repository.recipe;

import com.ronbodnar.reciperepository.model.recipe.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {

    Optional<Ingredient> findByName(String name);
}