package com.ronbodnar.recipes.image.storage;

import com.ronbodnar.recipes.image.Image;

import java.util.concurrent.CompletableFuture;

public interface ImageStorage {

    CompletableFuture<Void> uploadImageAsync(Image image);

    CompletableFuture<Void> deleteImageWithKeyAsync(String storageKey);

}
