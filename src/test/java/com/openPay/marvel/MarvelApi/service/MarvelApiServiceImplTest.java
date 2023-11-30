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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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

        // Llama al método que deseas probar
        List<MarvelCharacter> characters = marvelApiService.getAllCharactersFromApi();

        // Realiza las aserciones necesarias
        // Verifica que la lista de personajes no esté vacía
        assertFalse(characters.isEmpty());
        // Verifica que la lista tenga la cantidad correcta de personajes (en este caso, 2)
        assertEquals(2, characters.size());
        // Puedes realizar más aserciones según la estructura esperada de los personajes
    }

    @Test
    public void testGetCharacterByIdFromApi() {
        // Configura el comportamiento del restTemplate mock
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(createMockJsonNode()));

        // Llama al método que deseas probar
        MarvelCharacter character = marvelApiService.getCharacterByIdFromApi(1011334L);

        // Realiza las aserciones necesarias
        // Verifica que el personaje no sea nulo
        assertNotNull(character);
        // Verifica que el ID del personaje sea el esperado
        assertEquals(1011334L, character.getId().longValue());
        // Puedes realizar más aserciones según la estructura esperada del personaje
    }

    // Métodos de utilidad para crear un JsonNode simulado
    private JsonNode createMockJsonNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        // Crea el nodo "data" y agrega la información del personaje
        ObjectNode dataNode = objectMapper.createObjectNode();

        // Simula una lista de personajes
        ArrayNode charactersArray = objectMapper.createArrayNode();
        ObjectNode characterNode = objectMapper.createObjectNode();
        characterNode.put("id", 1011334L);
        characterNode.put("name", "Iron Man");
        // Agrega otros campos según la estructura de tus objetos MarvelCharacter
        charactersArray.add(characterNode);

        // Agrega el nodo "results" al nodo "data"
        dataNode.set("results", charactersArray);

        // Agrega el nodo "data" al JSON principal
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
        // Agrega otros campos según la estructura de tus objetos MarvelCharacter
        charactersArray.add(character1);

        ObjectNode character2 = objectMapper.createObjectNode();
        character2.put("id", 1017100);
        character2.put("name", "Spider-Man");
        // Agrega otros campos según la estructura de tus objetos MarvelCharacter
        charactersArray.add(character2);

        // Crea el nodo "data" y agrega la lista de personajes
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.set("results", charactersArray);

        // Agrega el nodo "data" al JSON principal
        jsonNode.set("data", dataNode);

        return jsonNode;
    }
}
