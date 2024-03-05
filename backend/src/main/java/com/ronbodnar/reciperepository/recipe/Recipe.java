package com.ronbodnar.reciperepository.recipe;

import com.fasterxml.jackson.annotation.*;
import com.ronbodnar.reciperepository.attribute.Attribute;
import com.ronbodnar.reciperepository.cuisine.Cuisine;
import com.ronbodnar.reciperepository.imagedata.ImageData;
import com.ronbodnar.reciperepository.ingredient.Ingredient;
import com.ronbodnar.reciperepository.instruction.Instruction;
import com.ronbodnar.reciperepository.mealtype.MealType;
import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import com.ronbodnar.reciperepository.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
//@JsonPropertyOrder({})
@Table(name = "recipes")
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

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany
    @JoinTable(
            name = "recipe_preparation_types",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "preparation_type_id"))
    private Set<PreparationType> preparationTypes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_meals",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_type_id"))
    private Set<MealType> mealTypes = new HashSet<>();

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
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "recipe_instructions",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "instruction_id")
    )
    private List<Instruction> instructions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "recipe_images",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<ImageData> images = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag", nullable = false)
    private Set<String> tags = new HashSet<>();

}