package com.ronbodnar.reciperepository.authentication;

import com.ronbodnar.reciperepository.role.Role;
import com.ronbodnar.reciperepository.user.User;
import com.ronbodnar.reciperepository.payload.request.LoginRequest;
import com.ronbodnar.reciperepository.payload.request.RegisterRequest;
import com.ronbodnar.reciperepository.payload.response.FieldError;
import com.ronbodnar.reciperepository.role.RoleRepository;
import com.ronbodnar.reciperepository.user.UserRepository;
import com.ronbodnar.reciperepository.security.service.JwtService;
import com.ronbodnar.reciperepository.security.service.UserDetailsImpl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.*;

@Service
public class AuthenticationService {

    private final JwtService jwtService;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(JwtService jwtService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> authenticate(LoginRequest loginRequest) {
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = this.jwtService.generateCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(userDetails);
    }

    public ResponseEntity<?> register(RegisterRequest registerRequest, Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(errors.getAllErrors());

        List<FieldError> fieldErrorList = new ArrayList<FieldError>();
        if (this.userRepository.existsByUsername(registerRequest.getUsername()))
            fieldErrorList.add(new FieldError("username", "Username has already been registered."));

        if (this.userRepository.existsByEmail(registerRequest.getEmail()))
            fieldErrorList.add(new FieldError("email", "E-mail address has already been registered."));

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            fieldErrorList.add(new FieldError("password", "Password fields do not match."));
            fieldErrorList.add(new FieldError("confirmPassword", "Password fields do not match."));
        }

        if (!fieldErrorList.isEmpty())
            return ResponseEntity.badRequest().body(fieldErrorList);

        Set<Role> roles = new HashSet<>();
        Role userRole = this.roleRepository.findByRoleType(Role.RoleType.STANDARD).orElse(null);

        roles.add(userRole);

        String encodedPassword = this.passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRoles(roles);

        this.userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> deauthenticate() {
        ResponseCookie cookie = this.jwtService.generateDefaultCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("");
    }

}