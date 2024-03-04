package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.ImageData;
import org.springframework.data.repository.CrudRepository;

public interface RecipeImageRepository extends CrudRepository<ImageData, Long> {
}
