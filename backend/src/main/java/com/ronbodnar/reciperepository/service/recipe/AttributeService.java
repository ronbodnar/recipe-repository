package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.AttributeRepository;
import org.springframework.stereotype.Service;

@Service
public class AttributeService {

    private final AttributeRepository attributeRepository;

    public AttributeService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }
}
