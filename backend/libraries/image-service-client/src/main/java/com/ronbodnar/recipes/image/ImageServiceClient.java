package com.ronbodnar.recipes.image;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Set;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.ClientAttributes.clientRegistrationId;

@Component
public class ImageServiceClient {

    private final String imageService;

    private final RestClient restClient;

    public ImageServiceClient(
            @Value("${app.services.image-service}") String imageService,
            RestClient restClient
    ) {
        this.imageService = imageService;
        this.restClient = restClient;
    }

    public void markForDeletion(Set<UUID> imageIds) {
        try {
            restClient.post()
                    .uri(imageService + "/api/v1/images/mark-for-deletion")
                    .attributes(clientRegistrationId("keycloak"))
                    .body(imageIds)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MARK_IMAGES_FOR_DELETION_FAILED, e.getMessage());
        }
    }

    public void attach(Set<UUID> imageIds) {
        try {
            restClient.post()
                    .uri(imageService + "/api/v1/images/attach")
                    .attributes(clientRegistrationId("keycloak"))
                    .body(imageIds)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ATTACH_IMAGES_FAILED, e.getMessage());
        }
    }

}
