package com.ronbodnar.recipes.config;

import com.ronbodnar.recipes.security.exception.AuthenticationExceptionHandler;
import com.ronbodnar.recipes.security.exception.AuthorizationExceptionHandler;
import com.ronbodnar.recipes.security.filter.JwtAuthenticationFilter;
import com.ronbodnar.recipes.security.adapter.SecurityUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthorizationExceptionHandler accessDeniedHandler;
    private final AuthenticationExceptionHandler authenticationEntryPoint;

    public SecurityConfig(AuthorizationExceptionHandler accessDeniedHandler,
                          AuthenticationExceptionHandler authenticationEntryPoint) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        exception -> exception.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        (authorize) ->
                                authorize
                                        .requestMatchers("/api/v1/auth/**").permitAll()
                                        .anyRequest().authenticated()
                );

        http.addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(SecurityUserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(
                userDetailsService
        );
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}