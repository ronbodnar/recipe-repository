package com.ronbodnar.reciperepository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String email;

    private String username;
    private String displayName;

    private String firstName;
    private String lastName;

    //TODO: password encryption
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Account(String username, String displayName, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("Account(id=%s, username=%s, displayName=%s, firstName=%s, lastName=%s, email=%s)",
                this.id, this.username, this.displayName, this.firstName, this.lastName, this.email);
    }

}