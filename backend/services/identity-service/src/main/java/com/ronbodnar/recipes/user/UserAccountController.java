package com.ronbodnar.recipes.user;

import com.ronbodnar.recipes.user.dto.UserAccountDTO;
import com.ronbodnar.recipes.user.dto.UserAccountSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/{subject}/summary")
    public UserAccountSummaryDTO getSummaryBySubject(@PathVariable String subject) {
        return userAccountService.getSummaryBySubject(subject);
    }

}