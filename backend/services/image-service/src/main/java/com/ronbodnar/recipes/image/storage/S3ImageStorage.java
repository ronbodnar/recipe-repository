package recipes.image.storage;

import com.ronbodnar.recipes.image.Image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class S3ImageStorage implements ImageStorage {

    private final S3AsyncClient s3AsyncClient;

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    public S3ImageStorage(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @Override
    public CompletableFuture<Void> uploadImageAsync(Image image) {
        if (bucketName == null || image == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Bucket name and image are mandatory")
            );
        }

        log.info("Uploading object to S3 bucket {} with key {}", bucketName, image.getStorageKey());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(image.getStorageKey())
                .contentType(image.getContentType())
                .contentLength(image.getSize())
                .build();

        return s3AsyncClient.putObject(request, AsyncRequestBody.fromFile(image.getTempFilePath()))
                .whenComplete((response, ex) -> {
                    if (ex == null) {
                        log.info("Key {} successfully uploaded to AWS S3", image.getStorageKey());
                    } else {
                        log.error("Error uploading object to S3 bucket {} with key {}: {}",
                                bucketName, image.getStorageKey(), ex.getMessage(), ex);
                    }
                })
                .thenAccept(response -> {});
    }

    @Override
    public CompletableFuture<Void> deleteImageWithKeyAsync(String storageKey) {
        if (bucketName == null || storageKey == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Bucket name, Image, and key are mandatory")
            );
        }

        log.info("Deleting object from S3 bucket {} with key {}", bucketName, storageKey);

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(storageKey).build();

        return s3AsyncClient.deleteObject(request)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Key {} successfully deleted from AWS S3", storageKey);
                    }  else {
                        log.error("Error deleting object from S3 bucket {} with key {}", bucketName, storageKey);
                    }
                })
                .thenAccept(res -> {});
    }

}
