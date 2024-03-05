package com.ronbodnar.reciperepository.cuisine;

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
@Table(name = "cuisines")
public class Cuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Kind kind;

    @JsonIgnore
    @ManyToMany(mappedBy = "cuisines")
    private Set<Recipe> recipes = new HashSet<>();

    public Cuisine(Kind kind) {
        this.kind = kind;
    }

    public static enum Kind {
        AFRICAN, AMERICAN, ARAB, AUSTRALIAN, BRAZILIAN, CARIBBEAN, CHINESE, FRENCH, GERMAN,
        GREEK, INDIAN, ITALIAN, JAPANESE, JEWISH, KOREAN, LEBANESE, MEXICAN, SPANISH, THAI,
        TURKISH, VIETNAMESE
    }
}