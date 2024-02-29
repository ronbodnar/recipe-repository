package com.ronbodnar.reciperepository.model.recipe;

import com.ronbodnar.reciperepository.enums.MeasurementType;
import jakarta.persistence.*;
import lombok.Data;
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

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "quantity")
    private double quantity;

    @Column(name = "measurement_type")
    private MeasurementType measurementType;

    public RecipeIngredient(double quantity, MeasurementType measurementType) {
        this.quantity = quantity;
        this.measurementType = measurementType;
    }

}
