package com.ronbodnar.reciperepository.model;

import com.ronbodnar.reciperepository.enums.PreparationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The step number in the order of instructions
    @Column(name = "step")
    private int step;

    @Column(name = "text")
    private String text;

    @Column(name = "preparation_type")
    private PreparationType preparationType;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
