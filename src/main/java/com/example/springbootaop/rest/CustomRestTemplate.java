package com.example.springbootaop.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomRestTemplate {
    private final RestTemplate restTemplate;

    public CustomRestTemplate(RestTemplateBuilder restTemplateBuilder, @Qualifier("restCustomizer") RestTemplateCustomizer restTemplateCustomizer) {
        this.restTemplate = restTemplateBuilder
                .customizers(restTemplateCustomizer)
                .build();
    }

    public <T> T exchange(String path, HttpMethod method, Object entity, Class<T> responseType) {
        return restTemplate.exchange(path, method, new HttpEntity<>(entity), responseType).getBody();
    }
}
