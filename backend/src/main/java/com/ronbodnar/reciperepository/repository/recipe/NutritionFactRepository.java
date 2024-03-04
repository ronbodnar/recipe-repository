package com.ronbodnar.reciperepository.repository.recipe;

import com.ronbodnar.reciperepository.model.recipe.NutritionFact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionFactRepository extends CrudRepository<NutritionFact, Long> { }