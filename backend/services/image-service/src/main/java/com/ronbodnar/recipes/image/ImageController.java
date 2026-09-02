package com.ronbodnar.recipes.image;

import com.ronbodnar.recipes.image.dto.ImageUploadRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public List<UUID> uploadImages(@RequestParam("purpose") ImagePurpose purpose,
                                   @RequestPart("files") List<MultipartFile> files
    ) {
        System.out.println("Received an image upload request");
        return imageService.processImages(files, purpose);
    }

    @PostMapping("/attach")
    public void attachImages(@RequestBody List<UUID> imageIds) {
        imageService.attachImages(imageIds);
    }
}
