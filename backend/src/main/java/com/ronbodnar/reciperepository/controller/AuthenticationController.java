package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.util.MessageResponse;
import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Getter
@CrossOrigin
@RestController
public class AuthenticationController {

    private final UserRepository userRepository;

    public AuthenticationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record LoginRequest(String username, String password) {}

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Trying to log in with credentials:");
        System.out.println("username=" + loginRequest.username + ", password=" + loginRequest.password);
        System.out.println();
        System.out.println("List of accounts in userRepository:");
        userRepository.findAll().forEach(System.out::println);

        User user = userRepository.findByUsername(loginRequest.username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("user not found"));
        }

        if (!user.getPassword().equals(loginRequest.password)) {
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("invalid credentials"));
        }

        return new ResponseEntity<>(userRepository.findByUsername(loginRequest.username()), HttpStatus.OK);
    }

}
