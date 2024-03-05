package com.ronbodnar.reciperepository.preparationtype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ronbodnar.reciperepository.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "preparation_types")
public class PreparationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Kind kind;

    @JsonIgnore
    @ManyToMany(mappedBy = "preparationTypes")
    private Set<Recipe> recipes = new HashSet<>();

    public PreparationType(Kind kind) {
        this.kind = kind;
    }

    public static enum Kind {
        AIR_FRYER, OVEN, CROCK_POT, INSTANT_POT, STOVE_TOP, BARBECUE;
    }

}
