package com.ronbodnar.reciperepository.nutritionfact;

import com.ronbodnar.reciperepository.nutritionfact.NutritionFact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionFactRepository extends CrudRepository<NutritionFact, Long> { }