package com.ai.SpringAiDemo;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = 
                new HttpComponentsClientHttpRequestFactory(httpClient());
        return new BufferingClientHttpRequestFactory(factory);
    }

    @Bean
    public org.apache.hc.client5.http.classic.HttpClient httpClient() {
        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofSeconds(30))
                        .setConnectTimeout(Timeout.ofSeconds(30))
                        .build())
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
    }
}
