package com.ronbodnar.reciperepository.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class AuthenticationController {

    @RequestMapping("/auth/login")
    public long authenticate() {
        return 1;
    }
}
