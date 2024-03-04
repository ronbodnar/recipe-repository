package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.model.recipe.Instruction;
import com.ronbodnar.reciperepository.repository.recipe.InstructionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstructionService {

    private final InstructionRepository instructionRepository;

    public InstructionService(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    public void save(Instruction instruction) {
        this.instructionRepository.save(instruction);
    }

    public void saveAll(Iterable<Instruction> instructions) {
        this.instructionRepository.saveAll(instructions);
    }

    /**
     * Handles the full process of creating valid Instruction entities from a list of partial Instructions.
     *
     * @param instructionList A list of incomplete Instruction entities from the recipe creation request, containing only the text and image data byte array.
     * @return A list of Instruction entities that have been created.
     */
    public List<Instruction> createAll(List<Instruction> instructionList) {
        List<Instruction> instructions = new ArrayList<>();

        instructionList.forEach(instructionData -> {
           String text = instructionData.getText();
           byte[] imageData = instructionData.getImageData(); // optional

            Instruction instruction = new Instruction(text, imageData);

            instructions.add(instruction);
        });

        return instructions;
    }
}
