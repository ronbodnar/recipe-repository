package com.ronbodnar.recipes.modules.image.storage;

import com.ronbodnar.recipes.modules.image.Image;

import java.util.concurrent.CompletableFuture;

public interface ImageStorage {

    CompletableFuture<Void> uploadImageAsync(Image image);

    CompletableFuture<Void> deleteImageWithKeyAsync(String storageKey);

}
