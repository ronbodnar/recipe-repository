package com.ronbodnar.recipes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class RedisConfig {

    private final String serverHost;
    private final int serverPort;

    public RedisConfig(
            @Value("${spring.data.redis.host}") String serverHost,
            @Value("${spring.data.redis.port}")  int serverPort
    ) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(serverHost, serverPort);

        return new JedisConnectionFactory(config);
    }

}
