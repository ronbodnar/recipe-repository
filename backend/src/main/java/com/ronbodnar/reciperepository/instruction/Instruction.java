package com.ronbodnar.reciperepository.instruction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ronbodnar.reciperepository.recipe.Recipe;
import jakarta.persistence.*;
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

    @Column(name = "`text`")
    private String text;

    @Column(name = "image_uri")
    private String imageUri;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Transient
    @JsonIgnore
    public byte[] imageData;

    public Instruction(String text, byte[] imageData) {
        this.text = text;
        this.imageData = imageData;
    }
}
