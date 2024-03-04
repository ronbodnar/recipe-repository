package com.ronbodnar.reciperepository.repository;

import com.ronbodnar.reciperepository.model.recipe.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
}
