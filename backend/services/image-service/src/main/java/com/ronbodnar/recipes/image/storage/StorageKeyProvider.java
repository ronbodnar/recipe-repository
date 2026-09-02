package com.ronbodnar.recipes.image.storage;

import java.util.UUID;

public abstract class StorageKeyProvider {

    public abstract String buildStorageKey(UUID imageId, String contentType);

    // Assumes contentType to be "image/jpeg" or "image/png"
    protected String getExtensionFromContentType(String contentType) {
        String extension = contentType.split(";")[0].trim().toLowerCase();
        System.out.println("Content type: " + contentType);
        return switch (extension) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };
    }


}
