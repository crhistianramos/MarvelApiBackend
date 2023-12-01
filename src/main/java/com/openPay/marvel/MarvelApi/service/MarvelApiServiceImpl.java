package com.openPay.marvel.MarvelApi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openPay.marvel.MarvelApi.model.Log;
import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    private final RestTemplateWrapper restTemplate;

    // Constructor para inyectar RestTemplateWrapper
    @Autowired
    public MarvelApiServiceImpl(LogRepository logRepository, RestTemplateWrapper restTemplate) {
        this.logRepository = logRepository;
        this.restTemplate = restTemplate;
    }
    @Autowired
    private LogRepository logRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${marvel.api.public-key}")
    private String publicKey;

    @Value("${marvel.api.private-key}")
    private String privateKey;

    @Value("${marvel.api.base-url}")
    private String baseUrl;
    private static final String CHARACTERS_URL = "/v1/public/characters";
    private static final Logger logger = LoggerFactory.getLogger(MarvelApiServiceImpl.class);

    @Override
    public List<MarvelCharacter> getAllCharactersFromApi() {
        // Captura CurrentTime para generar hash
        long timestamp = System.currentTimeMillis();
        String hash = generateHash(timestamp);

        // Guarda en BD H2 la hora de la consulta
        saveTimestamp(System.currentTimeMillis(), "Consulta personajes de manera general", null);

        // Construye la URL para obtener todos los personajes
        final String url = baseUrl + CHARACTERS_URL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("ts", timestamp)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash);


        // Almacena la URL construida en una variable local
        URI fullUri = builder.build().toUri();

        // Crea los encabezados necesarios
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Construye el objeto HttpEntity con encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Utiliza la URI completa en lugar de una cadena de texto
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                fullUri,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class
        );

        // Obtiene el nodo "data" de la respuesta
        JsonNode dataNode = responseEntity.getBody().get("data");

        // Verifica si data contiene una lista de personajes o un solo personaje
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
        try {
            long timestamp = System.currentTimeMillis();
            String hash = generateHash(timestamp);

            // Construye la URL para obtener un personaje específico
            String url = (baseUrl != null ? baseUrl : "") + (baseUrl != null && baseUrl.endsWith("/") ? "" : "/") + CHARACTERS_URL + "/" + id;
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("ts", timestamp)
                    .queryParam("apikey", publicKey)
                    .queryParam("hash", hash);

            // Construye la URI completa
            URI fullUri = builder.build().toUri();

            // Construye encabezados necesarios
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Construye Objeto HttpEntity con encabezados
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // Construye la solicitud HTTP
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                    fullUri,
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            );

            // Obtiene nodo "data" de la respuesta
            JsonNode dataNode = responseEntity.getBody().get("data");

            // Verifica si data contiene un solo personaje
            if (dataNode != null && dataNode.has("results") && dataNode.get("results").isArray()) {
                JsonNode resultsNode = dataNode.get("results").get(0); // Tomar el primer elemento del array
                // Convierte el objeto de un solo personaje
                MarvelCharacter marvelCharacter = objectMapper.convertValue(resultsNode, MarvelCharacter.class);
                // Guarda el timestamp y el id del personaje en el log
                saveTimestamp(timestamp, "Consulta de manera individual", marvelCharacter.getId());

                return marvelCharacter;
            }

            String errorMessage = "No se encontró el personaje con ID: " + id;
            logger.warn(errorMessage);

            // Devuelve null si no encuentra el personaje
            return null;
        } catch (HttpClientErrorException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        }
    }



    private String generateHash(long timestamp) {
        try {
            // Concatena timestamp, private key y public key
            String toHash = timestamp + privateKey + publicKey;

            // Utiliza MD5 para generar el hash
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

        // Establecer las variables opcionales
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
