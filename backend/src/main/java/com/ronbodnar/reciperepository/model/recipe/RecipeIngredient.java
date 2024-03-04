package com.ronbodnar.reciperepository.model.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "quantity")
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "measurement_id")
    private MeasurementType measurementType;

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, double quantity, MeasurementType measurementType) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measurementType = measurementType;
    }

}
