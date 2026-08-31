package com.ronbodnar.recipes.user;

import com.ronbodnar.recipes.user.dto.UserAccountDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccountDTO getOrCreateUserAccount(Jwt jwt) {
        UUID keycloakSubject = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject(), "JWT subject is missing")
        );

        UserAccount userAccount = userAccountRepository.findByKeycloakSubject(keycloakSubject)
                .orElseGet(() -> {
                    UserAccount user = new UserAccount();
                    user.setKeycloakSubject(keycloakSubject);

                    return userAccountRepository.save(user);
                });

        return UserAccountDTO.fromEntity(userAccount);
    }
}
