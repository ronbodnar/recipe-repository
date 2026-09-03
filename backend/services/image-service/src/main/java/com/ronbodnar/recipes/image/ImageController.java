package com.ronbodnar.recipes.image;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public List<UUID> uploadImages(
            @RequestParam("purpose") ImagePurpose purpose,
            @RequestPart("files") List<MultipartFile> files
    ) {
        System.out.println("Received an image upload request");
        return imageService.processImages(files, purpose);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('DELETE-IMAGE')")
    public void deleteImages(@RequestBody List<UUID> imageIds) {
        imageService.deleteImages(imageIds);
    }

    @PostMapping("/attach")
    @PreAuthorize("hasRole('ATTACH-IMAGE')")
    public void attachImages(@RequestBody List<UUID> imageIds) {
        imageService.attachImages(imageIds);
    }

    @PostMapping("/mark-for-deletion")
    @PreAuthorize("hasRole('DELETE-IMAGE')")
    public void markImagesForDeletion(@RequestBody List<UUID> imageIds) {
        imageService.markImagesForDeletion(imageIds);
    }
}
