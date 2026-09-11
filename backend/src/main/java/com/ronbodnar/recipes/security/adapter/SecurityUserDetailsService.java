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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Calling loadUserByUsername with username: " + username);
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return SecurityUserDetails.build(userAccount);
    }
}