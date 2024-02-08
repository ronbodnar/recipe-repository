package com.ronbodnar.reciperepository.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName, lastName;

    private String username;

    private String email;

    private String password;
}
