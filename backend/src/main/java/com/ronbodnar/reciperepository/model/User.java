package com.ronbodnar.reciperepository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name="users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Email(message = "Please provide a valid e-mail address.")
    private String email;

    @Size(min = 3, max = 15, message = "Username must be between 3-15 characters.")
    @Pattern(regexp = "^[a-z\\d]+[._]?[a-z\\d]*[_.]?[a-z\\d]*$", message = "invalid username format")
    private String username;

    @NotBlank(message = "Please provide your preferred first name.")
    private String firstName;

    @NotBlank(message = "Please provide your preferred last name.")
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 30, message = "Password must be between 8-30 characters.")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("User(id=%s, username=%s, firstName=%s, lastName=%s, email=%s, password=%s, roles=%s)",
                this.id, this.username, this.firstName, this.lastName, this.email, this.password, this.roles.toString());
    }

}