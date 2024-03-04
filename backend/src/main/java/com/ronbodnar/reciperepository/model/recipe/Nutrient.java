package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "nutrients")
public class Nutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "label")
    private String label;

    @Column(name = "daily_value")
    private double dailyValue;

    @ManyToOne
    @JoinColumn(name = "measurement_type_id")
    private MeasurementType measurementType;
}
