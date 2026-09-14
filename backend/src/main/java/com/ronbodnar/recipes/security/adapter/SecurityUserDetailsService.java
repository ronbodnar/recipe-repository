package com.ronbodnar.recipes.security.adapter;

import com.ronbodnar.recipes.modules.identity.user.UserAccount;
import com.ronbodnar.recipes.modules.identity.user.UserAccountRepository;

import org.jspecify.annotations.NullMarked;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public SecurityUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @NullMarked
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return SecurityUserDetails.build(userAccount);
    }
}