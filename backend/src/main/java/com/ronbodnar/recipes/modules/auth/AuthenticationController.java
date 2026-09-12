package com.ronbodnar.recipes.modules.auth;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.auth.dto.RegisterRequest;
import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountChangeRequest;
import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountDTO;
import com.ronbodnar.recipes.security.jwt.JwtCookieService;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationTokenType;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;
import com.ronbodnar.recipes.modules.auth.domain.TokenPair;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationResponse;
import com.ronbodnar.recipes.modules.auth.dto.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.Getter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Getter
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtCookieService jwtCookieService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(
            JwtCookieService jwtCookieService,
            AuthenticationService authenticationService
    ) {
        this.jwtCookieService = jwtCookieService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/me")
    public UserAccountDTO getCurrentUser(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return UserAccountDTO.from(authenticationService.getCurrentUser(userDetails));
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCurrentUser(@RequestBody @Valid UserAccountChangeRequest request, @AuthenticationPrincipal SecurityUserDetails securityUser) {
        authenticationService.updateCurrentUser(request, securityUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAccountDTO> login(
            @RequestBody @Valid LoginRequest loginDTO,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuthenticationResponse response = authenticationService.authenticate(loginDTO, ip, userAgent);

        HttpHeaders headers = jwtCookieService.createCookieHeaders(response.tokenPair());

        return ResponseEntity.ok().headers(headers).body(UserAccountDTO.from(response.userAccount()));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserAccountDTO> register(
            @RequestBody @Valid RegisterRequest registerRequest,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        AuthenticationResponse response = authenticationService.register(registerRequest, ip, userAgent);

        HttpHeaders headers = jwtCookieService.createCookieHeaders(response.tokenPair());

        return ResponseEntity.ok().headers(headers).body(UserAccountDTO.from(response.userAccount()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<UserAccountDTO> refresh(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String refreshToken = jwtCookieService.getTokenFromRequest(request, AuthenticationTokenType.REFRESH);

        AuthenticationResponse response = authenticationService.refreshAccessToken(refreshToken, ip, userAgent);

        HttpHeaders headers = jwtCookieService.createCookieHeaders(response.tokenPair());

        return ResponseEntity.ok().headers(headers).body(UserAccountDTO.from(response.userAccount()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        TokenPair tokenPair = new TokenPair("", "");

        HttpHeaders headers = jwtCookieService.createCookieHeaders(tokenPair);

        return ResponseEntity.ok().headers(headers).build();
    }
}