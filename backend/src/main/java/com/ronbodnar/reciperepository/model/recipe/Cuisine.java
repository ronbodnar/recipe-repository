package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private CuisineType cuisineType;

    public Cuisine(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public static enum CuisineType {
        AMERICAN, ITALIAN, MEXICAN, JAPANESE, INDIAN;
    }
}