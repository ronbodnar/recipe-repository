package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.Attribute;
import org.springframework.data.repository.CrudRepository;

public interface AttributeRepository extends CrudRepository<Attribute, Long> {
}
