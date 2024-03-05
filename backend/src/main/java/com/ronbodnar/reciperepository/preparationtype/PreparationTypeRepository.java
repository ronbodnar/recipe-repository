package com.ronbodnar.reciperepository.preparationtype;

import com.ronbodnar.reciperepository.preparationtype.PreparationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreparationTypeRepository extends CrudRepository<PreparationType, Long> { }