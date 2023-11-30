package com.openPay.marvel.MarvelApi.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class RestTemplateWrapperImpl implements RestTemplateWrapper {

    private final RestTemplate restTemplate;

    public RestTemplateWrapperImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }
}
