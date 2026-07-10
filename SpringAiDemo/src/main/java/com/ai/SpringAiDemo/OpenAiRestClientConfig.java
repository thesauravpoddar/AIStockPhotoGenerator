package com.ai.SpringAiDemo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiRestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory());
        
        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}
