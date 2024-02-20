package com.ronbodnar.reciperepository.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Please provide your preferred first name.")
    private String firstName;

    @NotBlank(message = "Please provide your preferred last name.")
    private String lastName;

    @Size(min = 3, max = 15, message = "Username must be between 3-15 characters.")
    @Pattern(regexp = "^[a-zA-Z\\d]+[._]?[a-zA-Z\\d]*[_.]?[a-zA-Z\\d]*$", message = "Username may only contain the special characters (. and _).")
    private String username;

    @Email(message = "Please provide a valid e-mail address.")
    private String email;

    //TODO: Require one special char and one number, one upper/one lowercase
    @Size(min = 8, max = 30, message = "Password must be between 8-30 characters.")
    private String password;

    @Size(min = 8, max = 30, message = "Password must be between 8-30 characters.")
    private String confirmPassword;
}
