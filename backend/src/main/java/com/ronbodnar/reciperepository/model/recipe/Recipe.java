package com.ronbodnar.reciperepository.model.recipe;

import com.fasterxml.jackson.annotation.*;
import com.ronbodnar.reciperepository.enums.PreparationType;
import com.ronbodnar.reciperepository.model.user.User;
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

    //@JsonProperty("author_id")
    //@JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_type")
    private PreparationType preparationType;

    @ManyToMany
    @JoinTable(
            name = "recipe_courses",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_cuisines",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id"))
    private Set<Cuisine> cuisines = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_attributes",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private Set<Attribute> attributes = new HashSet<>();

    @OrderColumn
    @OneToMany(mappedBy = "recipe")
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Instruction> instructions = new HashSet<>();

    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageData> images = new HashSet<>();

}