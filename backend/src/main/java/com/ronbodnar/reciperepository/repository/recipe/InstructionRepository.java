package com.ronbodnar.reciperepository.repository.recipe;

import com.ronbodnar.reciperepository.model.recipe.Instruction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends CrudRepository<Instruction, Long> { }