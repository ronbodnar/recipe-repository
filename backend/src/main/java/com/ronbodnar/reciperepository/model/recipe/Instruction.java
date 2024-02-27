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

    @Column(name = "preparation_type")
    private PreparationType preparationType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instruction_id", referencedColumnName = "id")
    private InstructionImage image;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
