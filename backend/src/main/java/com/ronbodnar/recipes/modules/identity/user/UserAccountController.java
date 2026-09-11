package com.ronbodnar.recipes.modules.identity.user;

import com.ronbodnar.recipes.modules.identity.user.dto.UserAccountSummaryDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/{id}/summary")
    public UserAccountSummaryDTO getSummaryBySubject(@PathVariable Long id) {
        return userAccountService.getSummaryById(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public UserAccount getUserById(@PathVariable Long id) {
        return userAccountService.getById(id);
    }


}