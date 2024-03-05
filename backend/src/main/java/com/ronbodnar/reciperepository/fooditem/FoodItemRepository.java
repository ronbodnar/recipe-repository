package com.ronbodnar.reciperepository.fooditem;

import com.ronbodnar.reciperepository.fooditem.FoodItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodItemRepository extends CrudRepository<FoodItem, Integer> {

    Optional<FoodItem> findByName(String name);
}