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
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com"}, allowedHeaders = "*", allowCredentials = "true")
public class AuthenticationController {

    /**
     * The AuthenticationService dependency to be injected.
     */
    private final AuthenticationService authenticationService;

    /**
     * Constructor to inject the AuthenticationService dependency.
     * @param authenticationService The AuthenticationService bean to inject.
     */
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Used to determine user details for the authenticated user, if authenticated.
     * @param user
     * @return the authenticated Principal (user) or null.
     */
    @GetMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    /**
     * @see AuthenticationService#deauthenticate()
     */
    @PostMapping("/signout")
    public ResponseEntity<?> deauthenticate() {
        return authenticationService.deauthenticate();
    }

    /**
     * 
     * @see AuthenticationService#authenticate(LoginRequest)
     * @param loginRequest
     * @return
     * @throws BadCredentialsException
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        return authenticationService.authenticate(loginRequest);
    }

    /**
     * @see AuthenticationService#register(RegisterRequest, Errors)
     *
     * @param registerRequest The details necessary for user registration.
     * @param errors The validation error interface.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, Errors errors) {
        return authenticationService.register(registerRequest, errors);
    }

    /**
     * Handle form field constraint exceptions to pass to the front end.
     *
     * @param ex The ConstraintViolationException that is being handled
     * @return A HashMap of errors mapped by field -> message
     */
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