package com.openPay.marvel.MarvelApi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestTemplateWrapperImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestTemplateWrapperImpl restTemplateWrapper;

    @Test
    public void testExchange() {
        // Arrange
        URI url = URI.create("http://example.com");
        HttpMethod method = HttpMethod.GET;
        HttpEntity<?> requestEntity = new HttpEntity<>(null);
        Class<String> responseType = String.class;

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Mocked response");

        // Configura el mock
        when(restTemplate.exchange(eq(url), eq(method), any(), eq(responseType)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<String> actualResponse = restTemplateWrapper.exchange(url, method, requestEntity, responseType);

        // Verifica que el método exchange del RestTemplate se llamó con los argumentos correctos
        verify(restTemplate).exchange(eq(url), eq(method), any(), eq(responseType));
        // Verifica que la respuesta devuelta coincida con el ResponseEntity esperado
        assert actualResponse != null;
        assert actualResponse.getBody() != null;
        assert actualResponse.getBody().equals("Mocked response");
    }
}
