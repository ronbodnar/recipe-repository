package com.ronbodnar.recipes.recipe.image;

import com.ronbodnar.recipes.image.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeImageService {

    private final ImageService imageService;

    private final RecipeImageRepository recipeImageRepository;

    public RecipeImageService(ImageService imageService, RecipeImageRepository recipeImageRepository) {
        this.imageService = imageService;
        this.recipeImageRepository = recipeImageRepository;
    }

    public List<RecipeImage> findAllByRecipeId(UUID recipeId) {
        return recipeImageRepository.findByRecipeId(recipeId);
    }

    public List<RecipeImage> findByRecipeIdIn(List<UUID> recipeIds) {
        return recipeImageRepository.findByRecipeIdIn(recipeIds);
    }

    public List<RecipeImage> createImages(UUID recipeId, List<MultipartFile> files) {
        List<UUID> processedImageIds = imageService.processImages(files);
        List<RecipeImage> processedImages = processedImageIds.stream().map(imageId -> new RecipeImage(recipeId, imageId)).toList();

        recipeImageRepository.saveAll(processedImages);

        return processedImages;
    }

    public List<RecipeImage> updateImages(UUID recipeId, Set<UUID> imageIdsToKeep, List<MultipartFile> files) {
        // From this list, we need to discard (and delete) any ids not in imageIdsToKeep
        List<RecipeImage> existingRecipeImages = recipeImageRepository.findByRecipeId(recipeId);

        List<RecipeImage> incomingImages = createImages(recipeId, files);

        Map<UUID, RecipeImage> existingImageMap = existingRecipeImages.stream().collect(Collectors.toMap(
                RecipeImage::getImageId, recipeImage -> recipeImage, (i1, i2) -> i1
        ));

        List<UUID> idsMarkedForRemoval = existingRecipeImages.stream()
                .map(RecipeImage::getImageId)
                .filter(i -> !imageIdsToKeep.contains(i))
                .toList();

        System.out.println("Update image request");
        System.out.println("Existing images: " + existingRecipeImages.stream().map(RecipeImage::getImageId).toList());
        System.out.println("ExistingImageMap: " + existingImageMap);
        System.out.println("Ids marked for removal: " + idsMarkedForRemoval);
        System.out.println("Image ids to keep: " + imageIdsToKeep);

        recipeImageRepository.deleteAllByImageIdIn(idsMarkedForRemoval);

        imageService.deleteImages(idsMarkedForRemoval);

        List<RecipeImage> updatedImages = new ArrayList<>(incomingImages);

        for (UUID imageId: imageIdsToKeep) {
            updatedImages.add(existingImageMap.get(imageId));
        }

        return updatedImages;
    }

}