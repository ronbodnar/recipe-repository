package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe_images")
public class RecipeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "image_data")
    private String imageData;

    public RecipeImage(Recipe recipe, String imageData) {
        this.recipe = recipe;
        this.imageData = imageData;
    }
}