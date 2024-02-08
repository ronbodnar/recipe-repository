package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.Role;
import com.ronbodnar.reciperepository.payload.request.LoginRequest;
import com.ronbodnar.reciperepository.payload.request.RegisterRequest;
import com.ronbodnar.reciperepository.repository.RoleRepository;
import com.ronbodnar.reciperepository.security.service.JwtService;
import com.ronbodnar.reciperepository.security.service.UserDetailsImpl;
import com.ronbodnar.reciperepository.payload.response.MessageResponse;
import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Getter
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtService.generateCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(userDetails);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("username exists"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("email exists"));
        }

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

        return ResponseEntity.ok(new MessageResponse("user registered successfully"));
    }

}
