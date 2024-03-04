package com.ronbodnar.reciperepository.model.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseType courseType;

    public Course(CourseType courseType) {
        this.courseType = courseType;
    }

    public static enum CourseType {

        // maybe this should be MealType? or separate these between (breakfast/lunch/dinner) and (snack, side dish, main course, etc.)
        BREAKFAST, LUNCH, DINNER, APPETIZER_SNACK, DESSERT, SIDE_DISH, MAIN_COURSE

    }
}