package com.openPay.marvel.MarvelApi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

@Service
public class MarvelApiServiceImpl implements MarvelApiService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${marvel.api.public-key}")
    private String publicKey;

    @Value("${marvel.api.private-key}")
    private String privateKey;

    @Value("${marvel.api.base-url}")
    private String baseUrl;

    @Override
    public List<MarvelCharacter> getAllCharactersFromApi() {
        long timestamp = System.currentTimeMillis();
        String hash = generateHash(timestamp);

        // Construir la URL para obtener todos los personajes
        String url = baseUrl + "/v1/public/characters";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("ts", timestamp)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash);

        // Crear los encabezados necesarios
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Construir el objeto HttpEntity con encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Hacer la solicitud HTTP
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                JsonNode.class
        );

        // Obtener el nodo "data" de la respuesta
        JsonNode dataNode = responseEntity.getBody().get("data");

        // Verificar si "data" contiene una lista de personajes o un solo personaje
        if (dataNode != null && dataNode.has("results")) {
            JsonNode resultsNode = dataNode.get("results");
            if (resultsNode.isArray()) {
                // Convertir la lista de personajes
                return objectMapper.convertValue(resultsNode, new TypeReference<List<MarvelCharacter>>() {});
            } else if (resultsNode.isObject()) {
                // Convertir el objeto de un solo personaje
                MarvelCharacter singleCharacter = objectMapper.convertValue(resultsNode, MarvelCharacter.class);
                return Collections.singletonList(singleCharacter);
            }
        }

        // Manejar otros casos según sea necesario
        return Collections.emptyList();
    }

    @Override
    public MarvelCharacter getCharacterByIdFromApi(Long id) {
        long timestamp = System.currentTimeMillis();
        String hash = generateHash(timestamp);

        // Construir la URL para obtener un personaje específico
        String url = baseUrl + "/v1/public/characters/" + id;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("ts", timestamp)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash);

        // Construir los encabezados necesarios
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Construir el objeto HttpEntity con encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Hacer la solicitud HTTP
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MarvelCharacter> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                MarvelCharacter.class
        );
        return responseEntity.getBody();
    }

    private String generateHash(long timestamp) {
        try {
            // Concatenar timestamp, private key y public key
            String toHash = timestamp + privateKey + publicKey;

            // Utilizar MD5 para generar el hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

            // Convertir el hash a formato hexadecimal
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b));
            }

            return hexHash.toString();
        } catch (Exception e) {
            // Manejar la excepción adecuadamente (puede ser NoSuchAlgorithmException)
            e.printStackTrace();
            return null;
        }
    }
}
