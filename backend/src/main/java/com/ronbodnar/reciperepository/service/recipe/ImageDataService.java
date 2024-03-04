package com.ronbodnar.reciperepository.service.recipe;

import com.ronbodnar.reciperepository.model.recipe.ImageData;
import com.ronbodnar.reciperepository.repository.recipe.ImageDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImageDataService {

    /**
     *
     */
    private final ImageDataRepository imageDataRepository;

    private static Logger logger = LoggerFactory.getLogger(ImageDataService.class);

    /**
     *
     * @param imageDataRepository
     */
    public ImageDataService(ImageDataRepository imageDataRepository) {
        this.imageDataRepository = imageDataRepository;
    }

    /**
     * Persist an ImageData entity to the database.
     * @param imageData The entity to persist.
     */
    public void save(ImageData imageData) {
        this.imageDataRepository.save(imageData);
    }

    /**
     * Persist a list of ImageData entities to the database.
     * @param imageData The list of ImageData entities to persist.
     */
    public void saveAll(Iterable<ImageData> imageData) {
        this.imageDataRepository.saveAll(imageData);
    }

    /**
     * Handles the full process of creating valid ImageData entities from a list of partial ImageData entities.
     *
     * @param imageDataList A list of incomplete ImageData entities from the recipe creation request, containing only the data byte array.
     * @return A list of ImageData entities that have been created.
     */
    public List<ImageData> createAll(List<ImageData> imageDataList) {
        imageDataList.forEach(data -> {
            try {
                BufferedImage image = getImageFromData(data.getData());
            } catch (IOException e) {
                logger.error("Could not create image from byte array:", e);
            }
        });
        return null;
    }

    /**
     * Create a BufferedImage from the raw binary image data byte array.
     * @param data The byte array of raw binary data.
     * @return The created BufferedImage.
     * @throws IOException
     */
    public BufferedImage getImageFromData(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        return ImageIO.read(inputStream);
    }

    public void writeToFile(BufferedImage image) {
        //TODO: plan file structure
    }
}
