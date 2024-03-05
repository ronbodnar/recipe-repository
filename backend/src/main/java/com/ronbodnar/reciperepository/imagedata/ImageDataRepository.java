package com.ronbodnar.reciperepository.imagedata;

import com.ronbodnar.reciperepository.imagedata.ImageData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDataRepository extends CrudRepository<ImageData, Long> {
}
