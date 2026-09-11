package com.ronbodnar.recipes.modules.auth;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.auth.dto.RegisterRequest;
import com.ronbodnar.recipes.modules.auth.refreshtoken.RefreshTokenService;
import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountService;
import com.ronbodnar.recipes.security.jwt.JwtCookieService;
import com.ronbodnar.recipes.security.jwt.JwtService;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationTokenType;
import com.ronbodnar.recipes.modules.auth.domain.TokenPair;
import com.ronbodnar.recipes.modules.auth.domain.AuthenticationResponse;
import com.ronbodnar.recipes.modules.auth.dto.LoginRequest;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import io.jsonwebtoken.Claims;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AuthenticationService {

    private final JwtService jwtService;

    private final UserAccountService userAccountService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            JwtService jwtService,
            UserAccountService userAccountService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager
    ) {
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public UserAccount getCurrentUser(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return userAccountService.getById(userDetails.getId());
    }

    public AuthenticationResponse authenticate(
            LoginRequest loginRequest,
            String ip,
            String userAgent
    ) {
        log.info(
                "Login attempt: username={}, password={}, deviceId={}, ip={}, userAgent={}",
                loginRequest.username(),
                loginRequest.password(),
                loginRequest.deviceId(),
                ip,
                userAgent
        );

        String username = loginRequest.username();

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                username,
                loginRequest.password()
        );

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationRequest);
        } catch (DisabledException e) {
            throw new BusinessException(
                    ErrorCode.AUTH_USER_BLOCKED,
                    "Login failed: username=%s, reason=%s"
                            .formatted(username, "User is disabled")
            );
        } catch (LockedException e) {
            throw new BusinessException(
                    ErrorCode.AUTH_USER_LOCKED,
                    "Login failed: username=%s, reason=%s"
                            .formatted(username, "User is locked")
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Login failed: username=%s, reason=%s"
                            .formatted(username, "Invalid credentials")
            );
        }

        SecurityUserDetails securityUser = (SecurityUserDetails) authentication.getPrincipal();

        if (securityUser == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_AUTHENTICATION,
                    "Authenticated principal is missing"
            );
        }

        TokenPair tokenPair = createTokenPair(securityUser, loginRequest.deviceId(), ip);

        log.info(
                "Login successful: id={}, username={}, ip={}",
                securityUser.getId(),
                username,
                ip
        );

        return new AuthenticationResponse(getCurrentUser(securityUser), tokenPair);
    }

    public AuthenticationResponse register(RegisterRequest registerRequest, String ip, String userAgent) {
        log.info("Register request: {}", registerRequest);

        UserAccount created = userAccountService.create(registerRequest);

        SecurityUserDetails securityUser = SecurityUserDetails.build(created);

        TokenPair tokenPair = createTokenPair(securityUser, registerRequest.deviceId(), ip);

        return new AuthenticationResponse(created, tokenPair);
    }

    @Transactional
    public AuthenticationResponse refreshAccessToken(String refreshToken, String ip, String userAgent) {
        log.info("Refresh attempt: ip={}, userAgent={}", ip, userAgent);

        if (jwtService.isInvalid(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Claims claims = jwtService.getClaims(refreshToken);

        String deviceIdClaim = claims.get("deviceId", String.class);
        if (deviceIdClaim == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_REFRESH_TOKEN,
                    "Token has valid claims, but deviceId is not one of them."
            );
        }

        UUID deviceId = UUID.fromString(deviceIdClaim);

        UserAccount userAccount = refreshTokenService.validateAndGetUser(refreshToken);

        SecurityUserDetails securityUser = SecurityUserDetails.build(userAccount);

        TokenPair tokenPair = createTokenPair(securityUser, deviceId, ip);

        log.info(
                "Refresh successful: id={}, username={}, ip={}, requestId={}\n",
                securityUser.getId(),
                securityUser.getUsername(),
                ip,
                deviceId
        );

        return new AuthenticationResponse(userAccount, tokenPair);
    }

    private TokenPair createTokenPair(SecurityUserDetails securityUser, UUID deviceId, String ip) {
        String accessToken = jwtService.generateAuthToken(
                AuthenticationTokenType.ACCESS,
                deviceId,
                securityUser,
                JwtCookieService.getCookieExpiration(AuthenticationTokenType.ACCESS)
        );

        String refreshToken = jwtService.generateAuthToken(
                AuthenticationTokenType.REFRESH,
                deviceId,
                securityUser,
                JwtCookieService.getCookieExpiration(AuthenticationTokenType.REFRESH)
        );

        refreshTokenService.createFromToken(refreshToken, ip);

        return new TokenPair(accessToken, refreshToken);
    }

}