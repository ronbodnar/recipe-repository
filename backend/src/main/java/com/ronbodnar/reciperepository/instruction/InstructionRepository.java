package com.ronbodnar.reciperepository.instruction;

import com.ronbodnar.reciperepository.instruction.Instruction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends CrudRepository<Instruction, Long> { }