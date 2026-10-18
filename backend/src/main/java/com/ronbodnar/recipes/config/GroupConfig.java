package com.ronbodnar.recipes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(
        value = "app.groups.enabled",
        havingValue = "true"
)
@ComponentScan(basePackages = "com.ronbodnar.recipes.modules.group")
@EnableJpaRepositories(basePackages = "com.ronbodnar.recipes.modules.group")
public class GroupConfig {
}