package com.ronbodnar.recipes.identity;

import com.ronbodnar.recipes.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.user.dto.UserAccountDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping("/me")
    public UserAccountDTO getAuthenticatedUser(@AuthenticationPrincipal Jwt jwt) {
        return identityService.getAuthenticatedUser(jwt);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthenticatedUser(
            @Valid @RequestBody UserAccountChangeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        identityService.updateAuthenticatedUser(request, jwt);
    }
}