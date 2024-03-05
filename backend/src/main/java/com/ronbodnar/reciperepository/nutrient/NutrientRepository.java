package com.ronbodnar.reciperepository.nutrient;

import com.ronbodnar.reciperepository.nutrient.Nutrient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientRepository extends CrudRepository<Nutrient, Long> { }