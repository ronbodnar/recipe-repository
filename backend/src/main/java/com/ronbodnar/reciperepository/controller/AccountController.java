package com.ronbodnar.reciperepository.controller;

import com.ronbodnar.reciperepository.repository.AccountRepository;
import com.ronbodnar.reciperepository.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    private final AccountRepository userRepository;

    public AccountController(AccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        return (List<Account>) userRepository.findAll();
    }

    @PostMapping("/accounts")
    void addAccount(@RequestBody Account account) {
        userRepository.save(account);
    }

    @DeleteMapping("/accounts/{id}")
    void removeAccount(@PathVariable long id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable long id) {
        Optional<Account> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @PutMapping("/accounts/{id}")
    Account updateAccount(@PathVariable long id, @RequestBody Account account) {
        Optional<Account> userToUpdate = userRepository.findById(id);
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setUsername(account.getUsername());
            userToUpdate.get().setDisplayName(account.getDisplayName());

            userToUpdate.get().setEmail(account.getEmail());

            userToUpdate.get().setFirstName(account.getFirstName());
            userToUpdate.get().setLastName(account.getLastName());

            userRepository.save(userToUpdate.get());

            return userToUpdate.get();
        }
        return null;
    }
}