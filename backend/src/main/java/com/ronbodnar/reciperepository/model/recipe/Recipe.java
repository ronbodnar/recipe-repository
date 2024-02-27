package com.ronbodnar.reciperepository.model.recipe;

import com.ronbodnar.reciperepository.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int prepTime;

    private int cookTime;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeImage> images = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    private Set<Instruction> instructions = new HashSet<>();

}