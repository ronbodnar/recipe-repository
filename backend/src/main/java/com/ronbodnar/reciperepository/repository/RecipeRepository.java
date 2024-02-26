package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> { }