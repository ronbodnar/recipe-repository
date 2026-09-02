package com.ronbodnar.recipes.image;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import com.ronbodnar.recipes.image.storage.ImageStorage;
import com.ronbodnar.recipes.image.storage.StorageKeyProvider;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ImageService {

    private final ImageStorage imageStorage;
    private final ImageRepository imageRepository;

    private final StorageKeyProvider storageKeyProvider;

    public ImageService(ImageStorage imageStorage, ImageRepository imageRepository, StorageKeyProvider storageKeyProvider) {
        this.imageStorage = imageStorage;
        this.imageRepository = imageRepository;
        this.storageKeyProvider = storageKeyProvider;
    }

    public List<UUID> processImages(List<MultipartFile> files, ImagePurpose purpose) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<Image> incomingImages = getProcessedImages(files, purpose);

        try {
            imageRepository.saveAll(incomingImages);

            uploadImages(incomingImages);

            return incomingImages.stream().map(Image::getId).toList();
        } catch (Exception e) {
            // Clean up S3 objects (hopefully)
            deleteImages(incomingImages.stream().map(Image::getId).toList());
            throw e;
        } finally {
            incomingImages.forEach(img -> {
                if (img.getTempFilePath() != null) {
                    try {
                        Files.deleteIfExists(img.getTempFilePath());
                    } catch (IOException ignored) {}
                }
            });
        }
    }

    public void attachImages(List<UUID> imageIds) {
        List<Image> images = imageRepository.findAllByIdIn(imageIds);
        images.forEach(image -> image.setStatus(ImageStatus.ATTACHED));
        imageRepository.saveAll(images);
    }

    public void deleteImages(List<UUID> imageIds) {
        List<Image> images = imageRepository.findAllByIdIn(imageIds);

        imageRepository.deleteAll(images);

        List<CompletableFuture<Void>> deleteFutures = images.stream()
                .map(Image::getStorageKey)
                .map(imageStorage::deleteImageWithKeyAsync)
                .toList();

        try {
            CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("One or more image deletions failed.", e);
        }
    }

    private List<Image> getProcessedImages(List<MultipartFile> imageFiles, ImagePurpose purpose) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return Collections.emptyList();
        }

        Tika tika = new Tika();

        List<Image> images = new ArrayList<>(imageFiles.size());

        for (MultipartFile imageFile : imageFiles) {
            Image image = ImageFactory.createFromMultipartFile(imageFile, purpose, tika);

            image.setStorageKey(storageKeyProvider.buildStorageKey(image.getId(), image.getContentType()));

            images.add(image);
        }

        return images;
    }

    private void uploadImages(List<Image> images) {
        List<CompletableFuture<Void>> uploadFutures = images.stream()
                .map(imageStorage::uploadImageAsync)
                .toList();

        try {
            CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED, "Failed to upload one or more images.");
        }
    }

}
