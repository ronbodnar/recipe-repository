package com.ronbodnar.reciperepository.measurementtype;

import com.ronbodnar.reciperepository.measurementtype.MeasurementTypeRepository;
import org.springframework.stereotype.Service;
@Service
public class MeasurementTypeService {

    private final MeasurementTypeRepository measurementTypeRepository;

    public MeasurementTypeService(MeasurementTypeRepository measurementTypeRepository) {
        this.measurementTypeRepository = measurementTypeRepository;
    }
    
}