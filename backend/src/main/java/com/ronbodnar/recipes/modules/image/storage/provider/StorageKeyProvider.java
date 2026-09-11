package com.ronbodnar.recipes.modules.image.storage.provider;

import com.ronbodnar.recipes.modules.image.ImagePurpose;

import java.util.UUID;

public abstract class StorageKeyProvider {

    public abstract String buildStorageKey(UUID imageId, String contentType, ImagePurpose purpose);

    protected String getExtensionFromContentType(String contentType) {
        String extension = contentType.split(";")[0].trim().toLowerCase();
        return switch (extension) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };
    }


}
