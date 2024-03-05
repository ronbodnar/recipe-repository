package com.ronbodnar.reciperepository.attribute;

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
@Table(name = "attributes")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Kind kind;

    @JsonIgnore
    @ManyToMany(mappedBy = "attributes")
    private Set<Recipe> recipes = new HashSet<>();

    public Attribute(Kind kind) {
        this.kind = kind;
    }

    public static enum Kind {
        GLUTEN_FREE, DAIRY_FREE, KETO_FRIENDLY;
    }
}