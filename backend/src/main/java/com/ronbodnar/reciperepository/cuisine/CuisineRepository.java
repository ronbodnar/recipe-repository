package com.ronbodnar.reciperepository.cuisine;

import com.ronbodnar.reciperepository.cuisine.Cuisine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuisineRepository extends CrudRepository<Cuisine, Long> {

    Optional<Cuisine> findByKind(Cuisine.Kind kind);
}
