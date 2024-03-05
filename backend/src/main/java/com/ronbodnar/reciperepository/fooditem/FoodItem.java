package com.ronbodnar.reciperepository.fooditem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ronbodnar.reciperepository.ingredient.Ingredient;
import com.ronbodnar.reciperepository.nutritionfact.NutritionFact;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "food_item_nutrition_facts",
            joinColumns = @JoinColumn(name = "food_item_id"),
            inverseJoinColumns = @JoinColumn(name = "nutrition_fact_id")
    )
    private Set<NutritionFact> nutritionFacts;

    @JsonIgnore
    @OneToMany(mappedBy = "foodItem")
    private Set<Ingredient> ingredients = new HashSet<>();

    public FoodItem(String name, Set<NutritionFact> nutritionFacts) {
        this.name = name;
        this.nutritionFacts = nutritionFacts;
    }

}