package com.ronbodnar.recipes.image.storage;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class S3StorageKeyProvider extends StorageKeyProvider {

    public String buildStorageKey(UUID imageId, String contentType) {
        String path = "recipe-images/" + imageId.toString();
        String extension = getExtensionFromContentType(contentType);
        return path + "/original." + extension;
    }

}
