package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.RecipeImage;
import org.springframework.data.repository.CrudRepository;

public interface RecipeImageRepository extends CrudRepository<RecipeImage, Long> {
}
