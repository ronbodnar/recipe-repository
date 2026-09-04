package com.ronbodnar.recipes.recipe;

import com.ronbodnar.recipes.recipe.domain.*;
import com.ronbodnar.recipes.recipe.dto.RecipeRequest;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe")
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    @NotBlank
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "author_subject", nullable = false)
    private String authorSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private RecipeVisibility visibility;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderColumn(name = "display_order")
    private List<RecipeVariant> variants = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "recipe_image",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "image_id")
    private List<UUID> imageIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Course.class)
    @CollectionTable(
            name = "recipe_course",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "course")
    private Set<Course> courses = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Cuisine.class)
    @CollectionTable(
            name = "recipe_cuisine",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "cuisine")
    private Set<Cuisine> cuisines = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = MealType.class)
    @CollectionTable(
            name = "recipe_meal_type",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "meal_type")
    private Set<MealType> mealTypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = DietType.class)
    @CollectionTable(
            name = "recipe_diet_type",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "diet_type")
    private Set<DietType> dietTypes = new HashSet<>();

    public void addVariant(RecipeVariant recipeVariant) {
        if (recipeVariant.getId() == null) {
            recipeVariant.setRecipe(this);
            variants.add(recipeVariant);
        }
    }

    public Recipe(RecipeRequest recipeRequest) {
        this.title = recipeRequest.title();
        this.description = recipeRequest.description();
        this.visibility = recipeRequest.visibility();
        this.imageIds = recipeRequest.imageIds();
        this.cuisines = recipeRequest.cuisines();
        this.mealTypes = recipeRequest.mealTypes();
        this.courses = recipeRequest.courses();
        this.dietTypes = recipeRequest.dietTypes();
    }

    public void updateFrom(RecipeRequest patch, List<RecipeVariant> patchVariants) {
        if (patch.title() != null && !patch.title().isBlank()) {
            this.title = patch.title();
        }

        if (patch.description() != null && !patch.description().isBlank()) {
            this.description = patch.description();
        }

        if (patch.visibility() != null) {
            this.visibility = patch.visibility();
        }

        if (patch.imageIds() != null) {
            this.imageIds.clear();
            this.imageIds.addAll(patch.imageIds());
        }

        if (patchVariants != null && !patchVariants.isEmpty()) {
            this.variants.clear();
            patchVariants.forEach(this::addVariant);
        }

        if (patch.cuisines() != null && !patch.cuisines().isEmpty()) {
            this.cuisines.clear();
            this.cuisines.addAll(patch.cuisines());
        }

        if (patch.mealTypes() != null && !patch.mealTypes().isEmpty()) {
            this.mealTypes.clear();
            this.mealTypes.addAll(patch.mealTypes());
        }

        if (patch.courses() != null && !patch.courses().isEmpty()) {
            this.courses.clear();
            this.courses.addAll(patch.courses());
        }

        if (patch.dietTypes() != null && !patch.dietTypes().isEmpty()) {
            this.dietTypes.clear();
            this.dietTypes.addAll(patch.dietTypes());
        }
    }

    @Override
    public String toString() {
        return "Recipe={" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", visibility=" + visibility +
                ", authorSubject=" + authorSubject +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", variants=" + variants +
                ", cuisines=" + cuisines +
                ", mealTypes=" + mealTypes +
                ", dishTypes=" + courses +
                ", dietTypes=" + dietTypes +
                "}";
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof Recipe otherRecipe)) return false;

        return id != null && Objects.equals(id, otherRecipe.id);
    }

}