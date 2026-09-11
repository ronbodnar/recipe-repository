package com.ronbodnar.recipes.modules.recipe;

import com.ronbodnar.recipes.modules.recipe.domain.CookingMethod;
import com.ronbodnar.recipes.modules.recipe.dto.RecipeVariantRequest;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe_variant")
public class RecipeVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "name")
    @Size(max = 50)
    private String name;

    @Column(name = "prep_time")
    @Min(0)
    @Max(2880)
    private int prepTime;

    @Column(name = "cook_time")
    @Min(0)
    @Max(2880)
    private int cookTime;

    @Column(name = "yield")
    @Size(max = 50)
    private String yield;

    @Enumerated(EnumType.STRING)
    @Column(name = "cooking_method")
    @NotNull
    private CookingMethod cookingMethod;

    @ElementCollection
    @CollectionTable(name = "recipe_variant_ingredient")
    @OrderColumn(name = "display_order")
    @Column(name = "text")
    private List<String> ingredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_variant_instruction")
    @OrderColumn(name = "display_order")
    @Column(name = "text")
    private List<String> instructions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_variant_note")
    @OrderColumn(name = "display_order")
    @Column(name = "text")
    private List<String> notes = new ArrayList<>();

    public RecipeVariant(RecipeVariantRequest recipeVariantRequest) {
        this.name = recipeVariantRequest.name();
        this.prepTime = recipeVariantRequest.prepTime();
        this.cookTime = recipeVariantRequest.cookTime();
        this.yield = recipeVariantRequest.yield();
        this.cookingMethod = recipeVariantRequest.cookingMethod();
        this.ingredients = recipeVariantRequest.ingredients();
        this.instructions = recipeVariantRequest.instructions();
        this.notes = recipeVariantRequest.notes();
    }

}