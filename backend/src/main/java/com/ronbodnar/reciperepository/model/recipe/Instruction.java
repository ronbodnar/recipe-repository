package com.ronbodnar.reciperepository.model.recipe;

import com.ronbodnar.reciperepository.enums.PreparationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "instructions")
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_number")
    private int stepNumber;

    @Column(name = "content")
    private String content;

    @Column(name = "preparation_type")
    private PreparationType preparationType;

    @Column(name = "image_data")
    private String imageData;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public Instruction(Recipe recipe, int stepNumber, String content, PreparationType preparationType, String imageData) {
        this.recipe = recipe;
        this.stepNumber = stepNumber;
        this.content = content;
        this.preparationType = preparationType;
        this.imageData = imageData;
    }
}
