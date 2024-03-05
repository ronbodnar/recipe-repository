package com.ronbodnar.reciperepository.fooditem;

import com.ronbodnar.reciperepository.fooditem.FoodItemRepository;
import org.springframework.stereotype.Service;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }
}
