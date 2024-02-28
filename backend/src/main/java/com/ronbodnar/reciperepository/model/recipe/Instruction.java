package com.ronbodnar.reciperepository.model.recipe;

import com.ronbodnar.reciperepository.enums.PreparationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "instructions")
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order")
    private int order;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(name = "preparation_type")
    private PreparationType preparationType;

    @Column(name = "image_data")
    private String imageData;
    // Base64 representation of the image
}
