package com.openPay.marvel.MarvelApi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openPay.marvel.MarvelApi.model.Log;
import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
public class MarvelApiServiceImpl implements MarvelApiService {

    @Autowired
    private LogRepository logRepository;
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

        //Guarda en BD H2 la hora de la consulta
        saveTimestamp(System.currentTimeMillis(), null, null);

        // Construye la URL para obtener todos los personajes
        String url = baseUrl + "/v1/public/characters";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("ts", timestamp)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash);

        // Crea los encabezads necesarios
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Construye el objeto HttpEntity con encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Contruye la solicitud HTTP
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                JsonNode.class
        );

        // Obtiene el nodo "data" de la respuesta
        JsonNode dataNode = responseEntity.getBody().get("data");

        // Verifica si "data" contiene una lista de personajes o un solo personaje
        if (dataNode != null && dataNode.has("results")) {
            JsonNode resultsNode = dataNode.get("results");

            if (resultsNode.isArray()) {
                // Convierte la lista de personajes
                return objectMapper.convertValue(resultsNode, new TypeReference<List<MarvelCharacter>>() {});
            } else if (resultsNode.isObject()) {
                // Convierte el objeto de un solo personaje
                MarvelCharacter singleCharacter = objectMapper.convertValue(resultsNode, MarvelCharacter.class);
                return Collections.singletonList(singleCharacter);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public MarvelCharacter getCharacterByIdFromApi(Long id) {
        long timestamp = System.currentTimeMillis();
        String hash = generateHash(timestamp);

        // Construye la URL para obtener un personaje específico
        String url = baseUrl + "/v1/public/characters/" + id;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("ts", timestamp)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash);

        // Construye encabezados necesarios
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Construye Objeto HttpEntity con encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Construye la solicitud HTTP
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                JsonNode.class
        );

        // Obtiene nodo "data" de la respuesta
        JsonNode dataNode = responseEntity.getBody().get("data");

        // Verifica si "data" contiene un solo personaje
        if (dataNode != null && dataNode.has("results") && dataNode.get("results").isArray()) {
            JsonNode resultsNode = dataNode.get("results").get(0); // Tomar el primer elemento del array
            // Convierte el objeto de un solo personaje
            MarvelCharacter marvelCharacter = objectMapper.convertValue(resultsNode, MarvelCharacter.class);

            // Guarda el timestamp y el id del personaje en el log
            saveTimestamp(timestamp, "getCharacterByIdFromApi", marvelCharacter.getId());

            return marvelCharacter;
        }

        return null;
    }


    private String generateHash(long timestamp) {
        try {
            // Concatena timestamp, private key y public key
            String toHash = timestamp + privateKey + publicKey;

            // Utilizar MD5 para generar el hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

            // Convierte el hash a formato hexadecimal
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b));
            }

            return hexHash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveTimestamp(long timestamp, String service, Long characterId) {
        // Convertir el timestamp a LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());

        // Crear el objeto Log y establecer la timestamp
        Log log = new Log();
        log.setTimestamp(localDateTime);

        // Establecer las variables opcionales si están presentes
        if (service != null) {
            log.setService(service);
        }

        if (characterId != null) {
            log.setCharacterId(characterId);
        }

        // Guardar en el repositorio
        logRepository.save(log);
    }

}
