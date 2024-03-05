package com.ronbodnar.reciperepository.measurementtype;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "measurement_types")
public class MeasurementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Kind kind;

    public MeasurementType(String kind) {
        this.kind = Kind.valueOf(kind);
    }

    public MeasurementType(Kind kind) {
        this.kind = kind;
    }

    public static enum Kind {
        DASH,
        TEASPOON,
        TABLESPOON,
        CUP,
        GRAM,
        OUNCE,
        POUND,
        PINT,
        QUART,
        FLUID_OUNCE;
    }

}
