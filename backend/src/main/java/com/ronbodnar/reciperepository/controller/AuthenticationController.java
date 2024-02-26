package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.Role;
import com.ronbodnar.reciperepository.payload.request.LoginRequest;
import com.ronbodnar.reciperepository.payload.request.RegisterRequest;
import com.ronbodnar.reciperepository.payload.response.FieldError;
import com.ronbodnar.reciperepository.repository.RoleRepository;
import com.ronbodnar.reciperepository.security.service.JwtService;
import com.ronbodnar.reciperepository.security.service.UserDetailsImpl;
import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Getter
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com", "https://ronbodnar.github.io"}, allowedHeaders = "*", allowCredentials = "true")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(JwtService jwtService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth/user")
    public Principal user(Principal user) {
        return user;
    }

    @PostMapping("/auth/revoke")
    public ResponseEntity<?> revoke() {
        ResponseCookie cookie = jwtService.generateDefaultCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        logger.info("Authentication: " + authentication.toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtService.generateCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(userDetails);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(errors.getAllErrors());

        List<FieldError> fieldErrorList = new ArrayList<FieldError>();
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            fieldErrorList.add(new FieldError("username", "Username has already been registered."));

        if (userRepository.existsByEmail(registerRequest.getEmail()))
            fieldErrorList.add(new FieldError("email", "E-mail address has already been registered."));

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            fieldErrorList.add(new FieldError("password", "Password fields do not match."));
            fieldErrorList.add(new FieldError("confirmPassword", "Password fields do not match."));
        }

        if (!fieldErrorList.isEmpty())
            return ResponseEntity.badRequest().body(fieldErrorList);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);

        roles.add(userRole);

        String encodedPassword = new BCryptPasswordEncoder().encode(registerRequest.getPassword());

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok().body("");
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
