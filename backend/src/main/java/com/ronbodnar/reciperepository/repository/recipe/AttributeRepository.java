package com.ronbodnar.reciperepository.repository.recipe;

import com.ronbodnar.reciperepository.model.recipe.Attribute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeRepository extends CrudRepository<Attribute, Long> {

    Optional<Attribute> findByKind(Attribute.Kind kind);
}
