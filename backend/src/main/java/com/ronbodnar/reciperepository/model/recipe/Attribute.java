package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attributes")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AttributeType attributeType;

    public Attribute(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public static enum AttributeType {
        GLUTEN_FREE, DAIRY_FREE, KETO_FRIENDLY;
    }
}