package com.ronbodnar.recipes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class AwsFactoryConfig {

    @Bean
    public static S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.US_WEST_2)
                .httpClientBuilder(NettyNioAsyncHttpClient.builder())
                .build();
    }
}
