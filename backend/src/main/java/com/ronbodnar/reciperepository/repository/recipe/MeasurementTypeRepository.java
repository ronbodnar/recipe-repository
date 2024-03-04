package com.ronbodnar.reciperepository.repository.recipe;

import com.ronbodnar.reciperepository.model.recipe.MeasurementType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeasurementTypeRepository extends CrudRepository<MeasurementType, Long> {

    Optional<MeasurementType> findByKind(MeasurementType.Kind kind);
}