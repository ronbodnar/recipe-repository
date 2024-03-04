package com.ronbodnar.reciperepository.model.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ingredient_nutrition_facts",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "nutrition_fact_id")
    )
    private Set<NutritionFact> nutritionFacts;

    @JsonIgnore
    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredient> ingredients = new HashSet<>();

}