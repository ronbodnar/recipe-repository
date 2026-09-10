package com.ronbodnar.recipes.identity;

import com.ronbodnar.recipes.common.exception.BusinessException;
import com.ronbodnar.recipes.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.security.oauth2.client.web.ClientAttributes.clientRegistrationId;

@Component
public class IdentityServiceClient {

    private final String identityService;

    private final RestClient restClient;

    public IdentityServiceClient(
            @Value("${app.services.identity-service}") String identityService,
            RestClient restClient
    ) {
        this.identityService = identityService;
        this.restClient = restClient;
    }

    public void isGroupMember(String authSubject, String groupName) {
        try {
            restClient.get()
                    .uri(identityService + "/api/v1/identity/groups/" + groupName)
                    .attributes(clientRegistrationId("keycloak"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MARK_IMAGES_FOR_DELETION_FAILED, e.getMessage());
        }
    }

}
