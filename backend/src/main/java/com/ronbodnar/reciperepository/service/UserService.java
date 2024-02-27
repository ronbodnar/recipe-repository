package com.ronbodnar.reciperepository.service;

import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }
}
