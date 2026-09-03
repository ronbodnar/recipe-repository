package com.ronbodnar.recipes.image;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImageCleanupTask {

    private final ImageService imageService;

    public ImageCleanupTask(ImageService imageService) {
        this.imageService = imageService;
    }

    @Scheduled(fixedDelayString = "${app.images.cleanup-interval}")
    public void cleanup() {
        this.imageService.cleanupImages();
    }
}
