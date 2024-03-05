package com.ronbodnar.reciperepository.mealtype;

import com.ronbodnar.reciperepository.mealtype.MealType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealTypeRepository extends CrudRepository<MealType, Long> {

    Optional<MealType> findByKind(MealType.Kind kind);
}
