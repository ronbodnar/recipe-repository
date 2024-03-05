package com.ronbodnar.reciperepository.mealtype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ronbodnar.reciperepository.recipe.Recipe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_types")
public class MealType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Kind kind;

    @JsonIgnore
    @ManyToMany(mappedBy = "mealTypes")
    private Set<Recipe> recipes = new HashSet<>();

    public MealType(Kind kind) {
        this.kind = kind;
    }

    public static enum Kind {
        BREAKFAST, LUNCH, BRUNCH, DINNER_SUPPER, SNACK

    }
}