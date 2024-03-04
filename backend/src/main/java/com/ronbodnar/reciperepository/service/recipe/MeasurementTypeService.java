package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.repository.recipe.MeasurementTypeRepository;
import org.springframework.stereotype.Service;
@Service
public class MeasurementTypeService {

    private final MeasurementTypeRepository measurementTypeRepository;

    public MeasurementTypeService(MeasurementTypeRepository measurementTypeRepository) {
        this.measurementTypeRepository = measurementTypeRepository;
    }
    
}