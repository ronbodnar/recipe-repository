package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.model.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @PostMapping("/users")
    void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @DeleteMapping("/users/{id}")
    void removeUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/users/{id}")
    User getUser(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable long id, @RequestBody User user) {
        Optional<User> userToUpdate = userRepository.findById(id);
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setUsername(user.getUsername());
            userToUpdate.get().setDisplayName(user.getDisplayName());

            userToUpdate.get().setEmail(user.getEmail());

            userToUpdate.get().setFirstName(user.getFirstName());
            userToUpdate.get().setLastName(user.getLastName());

            userRepository.save(userToUpdate.get());

            return userToUpdate.get();
        }
        return null;
    }
}