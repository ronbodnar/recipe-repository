package com.ronbodnar.reciperepository.security;

import com.ronbodnar.reciperepository.model.user.User;
import com.ronbodnar.reciperepository.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserRepository userRepository;

    public AuthenticationProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<User> user = userRepository.findByUsername(name);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(user.get(), password);
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}