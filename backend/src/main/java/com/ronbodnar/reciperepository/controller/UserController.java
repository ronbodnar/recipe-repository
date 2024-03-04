package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://ronbodnar.com"}, allowedHeaders = "*", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAll();
    }
}