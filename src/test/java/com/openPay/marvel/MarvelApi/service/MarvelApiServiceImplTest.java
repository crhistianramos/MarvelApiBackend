package com.openPay.marvel.MarvelApi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openPay.marvel.MarvelApi.model.Log;
import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class MarvelApiServiceImplTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MarvelApiServiceImpl marvelApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(marvelApiService, "baseUrl", "https://gateway.marvel.com:443");
        ReflectionTestUtils.setField(marvelApiService, "privateKey", "05023f9fa517d68781de9ccc07c3d91dece9b1c8");
        ReflectionTestUtils.setField(marvelApiService, "publicKey", "1c69002b40d451706a6cc7f63d0919f0");
    }

    @Test
    void getAllCharactersFromApi() {
        // Mockear la respuesta de la API
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(new ObjectMapper().createObjectNode().putArray("results")));

        // Llamar al método del servicio
        List<MarvelCharacter> characters = marvelApiService.getAllCharactersFromApi();

        // Verificar que se llamó al repositorio de logs
        verify(logRepository, times(1)).save(any(Log.class));

        // Verificar que se llamó a la API externa
    }

    @Test
    void getCharacterByIdFromApi() {
        // Mockear la respuesta de la API
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(new ObjectMapper().createObjectNode().putArray("results")));

        // Llamar al método del servicio
        MarvelCharacter character = marvelApiService.getCharacterByIdFromApi(1011334L);

        // Verificar que se llamó al repositorio de logs
        verify(logRepository, times(1)).save(any(Log.class));

        // Verificar que se llamó a la API externa
    }
}
