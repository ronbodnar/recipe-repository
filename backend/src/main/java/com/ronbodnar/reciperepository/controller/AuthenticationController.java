package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.payload.request.LoginRequest;
import com.ronbodnar.reciperepository.payload.request.RegisterRequest;
import com.ronbodnar.reciperepository.service.AuthenticationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Getter
@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com"}, allowedHeaders = "*", allowCredentials = "true")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/auth/user")
    public Principal user(Principal user) {
        return user;
    }

    @PostMapping("/auth/revoke")
    public ResponseEntity<?> revoke() {
        return authenticationService.revoke();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        return authenticationService.authenticate(loginRequest);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, Errors errors) {
        return authenticationService.register(registerRequest, errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(ConstraintViolationException ex) {
        // Handle ConstraintViolationExceptions and populate a map of any errors
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((violation) -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        return errors;
    }

}