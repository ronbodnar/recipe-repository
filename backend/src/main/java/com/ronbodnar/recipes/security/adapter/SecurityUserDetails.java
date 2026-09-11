package com.ronbodnar.recipes.security.adapter;

import com.ronbodnar.recipes.modules.identity.user.UserAccount;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SecurityUserDetails implements UserDetails {

    @EqualsAndHashCode.Include
    private final Long id;

    private final String username;

    @JsonIgnore
    @ToString.Exclude
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public static SecurityUserDetails build(UserAccount userAccount) {
        List<GrantedAuthority> authorities = userAccount.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toUnmodifiableList());

        return new SecurityUserDetails(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getPassword(),
                authorities);
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}