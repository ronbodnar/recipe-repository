package com.ronbodnar.reciperepository.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // define the security filters for http requests
        http
                .cors(Customizer.withDefaults()) // fixes cors issue with auth redirection
                .csrf(AbstractHttpConfigurer::disable) // disable csrf
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/**").permitAll() // allow all requests to all auth endpoints
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // allow all requests with OPTIONS method
                        .anyRequest().authenticated() // allow any request where the user is authenticated
                )
                .formLogin(form -> form.loginPage("/users/login").loginProcessingUrl("/auth/login").permitAll()) // form-based session authentication
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

}