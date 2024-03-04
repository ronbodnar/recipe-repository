package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.Cuisine;
import org.springframework.data.repository.CrudRepository;

public interface CuisineRepository extends CrudRepository<Cuisine, Long> {
}
