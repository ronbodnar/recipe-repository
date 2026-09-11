package com.ronbodnar.recipes.security.jwt;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetails;

import io.jsonwebtoken.Claims;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationMapper {

    public static UserDetails toUserDetails(Claims claims) {
        if (claims == null) {
            throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN, "Token claims missing while parsing token");
        }

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);

        List<LinkedHashMap<String, String>> roles = (List<LinkedHashMap<String, String>>) claims.get("roles");
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(r -> r.get("authority"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());

        return new SecurityUserDetails(id, username, null, authorities);
    }
}
