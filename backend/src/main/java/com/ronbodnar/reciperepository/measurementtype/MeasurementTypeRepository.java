package com.ronbodnar.reciperepository.measurementtype;

import com.ronbodnar.reciperepository.measurementtype.MeasurementType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeasurementTypeRepository extends CrudRepository<MeasurementType, Long> {

    Optional<MeasurementType> findByKind(MeasurementType.Kind kind);
}