package com.ronbodnar.reciperepository.attribute;

import com.ronbodnar.reciperepository.attribute.Attribute;
import com.ronbodnar.reciperepository.recipe.Recipe;
import com.ronbodnar.reciperepository.attribute.AttributeRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AttributeService {

    private final AttributeRepository attributeRepository;

    public AttributeService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    public Set<Attribute> createAll(Recipe recipe, Set<Attribute> attributeList) {
        return null;
    }
}
