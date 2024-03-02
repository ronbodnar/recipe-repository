package com.ronbodnar.reciperepository.model.recipe;

import com.fasterxml.jackson.annotation.*;
import com.ronbodnar.reciperepository.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipes")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@JsonPropertyOrder({ "id", "title", "description", "author_id" })
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "prep_time")
    private int prepTime;

    @Column(name = "cook_time")
    private int cookTime;

    @Column(name = "num_servings")
    private int servings;

    @JsonProperty("author_id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @JsonManagedReference
    @OneToMany(mappedBy = "recipe")
    private Set<RecipeImage> images;

    @JsonManagedReference
    @OneToMany(mappedBy = "recipe")
    private Set<Instruction> instructions;

    @JsonManagedReference
    @OneToMany(mappedBy = "recipe")
    private Set<RecipeIngredient> ingredients;

}