package com.ronbodnar.recipes.image.storage.provider.s3;

import com.ronbodnar.recipes.image.ImagePurpose;

import com.ronbodnar.recipes.image.storage.provider.StorageKeyProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class S3StorageKeyProvider extends StorageKeyProvider {

    public String buildStorageKey(UUID imageId, String contentType, ImagePurpose purpose) {
        String base = purpose == ImagePurpose.RECIPE ? "recipe-images" : "profile-images";
        String path = base + "/" + imageId.toString();
        String extension = getExtensionFromContentType(contentType);
        return path + "/original." + extension;
    }

}
