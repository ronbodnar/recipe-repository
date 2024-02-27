package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "instruction_images")
public class InstructionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @OneToOne(mappedBy = "image")
    private Instruction instruction;

    @Column(name = "image_url")
    private String imageUrl;
}