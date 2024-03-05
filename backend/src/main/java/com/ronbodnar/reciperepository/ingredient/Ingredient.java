package com.ronbodnar.reciperepository.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ronbodnar.reciperepository.fooditem.FoodItem;
import com.ronbodnar.reciperepository.measurementtype.MeasurementType;
import com.ronbodnar.reciperepository.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe_ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @Column(name = "quantity")
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "measurement_id")
    private MeasurementType measurementType;

    public Ingredient(Recipe recipe, FoodItem foodItem, double quantity, MeasurementType measurementType) {
        this.recipe = recipe;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.measurementType = measurementType;
    }

}
