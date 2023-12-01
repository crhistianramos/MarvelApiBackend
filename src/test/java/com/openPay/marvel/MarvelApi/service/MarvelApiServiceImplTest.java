package com.openPay.marvel.MarvelApi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MarvelApiServiceImplTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private RestTemplateWrapper restTemplate;

    @InjectMocks
    private MarvelApiServiceImpl marvelApiService;

    @Test
    public void testGetAllCharactersFromApi() {
        // Configura el comportamiento del restTemplate mock
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(createMockJsonNodeWithCharacters()));

        // Llama al método
        List<MarvelCharacter> characters = marvelApiService.getAllCharactersFromApi();

        // Verifica que la lista de personajes no esté vacía
        assertFalse(characters.isEmpty());
        // Verifica que la lista tenga la cantidad correcta de personajes (en este caso, 2)
        assertEquals(2, characters.size());
    }

    @Test
    public void testGetCharacterByIdFromApi() {
        // Configura el comportamiento del restTemplate mock
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(createMockJsonNode()));

        // Llama al método a probar
        MarvelCharacter character = marvelApiService.getCharacterByIdFromApi(1011334L);

        // Verifica que el personaje no sea nulo
        assertNotNull(character);
        // Verifica que el ID del personaje sea el esperado
        assertEquals(1011334L, character.getId().longValue());
    }

    // Metodo de que crea un nodo simulado
    private JsonNode createMockJsonNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        // Instancio información del personaje
        ObjectNode dataNode = objectMapper.createObjectNode();

        // Simula una lista de personajes
        ArrayNode charactersArray = objectMapper.createArrayNode();
        ObjectNode characterNode = objectMapper.createObjectNode();
        characterNode.put("id", 1011334L);
        characterNode.put("name", "Iron Man");
        charactersArray.add(characterNode);

        // Agrega el nodo
        dataNode.set("results", charactersArray);

        // Agrega al JSON principal
        jsonNode.set("data", dataNode);

        return jsonNode;
    }

    private JsonNode createMockJsonNodeWithCharacters() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        // Simula una lista de personajes
        ArrayNode charactersArray = objectMapper.createArrayNode();

        ObjectNode character1 = objectMapper.createObjectNode();
        character1.put("id", 1011334);
        character1.put("name", "Iron Man");
        charactersArray.add(character1);

        ObjectNode character2 = objectMapper.createObjectNode();
        character2.put("id", 1017100);
        character2.put("name", "Spider-Man");
        charactersArray.add(character2);

        // Crea el nodo y agrega la lista de personajes
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.set("results", charactersArray);

        // Agrega al JSON principal
        jsonNode.set("data", dataNode);

        return jsonNode;
    }

    @Test
    public void testRestTemplateWrapperExchange() {
        // Configura el mock
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(createMockJsonNodeWithCharacters()));

        // Llama al método
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                URI.create("http://example.com/api/characters"),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                JsonNode.class
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JsonNode dataNode = responseEntity.getBody().get("data");
        assertNotNull(dataNode);

        JsonNode resultsNode = dataNode.get("results");
        assertNotNull(resultsNode);
        assertTrue(resultsNode.isArray());
        assertEquals(2, resultsNode.size());
    }
}
