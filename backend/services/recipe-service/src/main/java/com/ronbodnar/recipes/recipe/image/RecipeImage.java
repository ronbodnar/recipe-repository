package com.ronbodnar.recipes.recipe.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe_image")
public class RecipeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private UUID recipeId;

    @Column(name = "image_id", nullable = false)
    private UUID imageId;

    public RecipeImage(UUID recipeId, UUID imageId) {
        this.recipeId = recipeId;
        this.imageId = imageId;
    }

}
