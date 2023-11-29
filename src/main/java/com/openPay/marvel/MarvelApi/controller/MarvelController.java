package com.openPay.marvel.MarvelApi.controller;


import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
import com.openPay.marvel.MarvelApi.service.MarvelApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marvel/characters")
public class MarvelController {

    private final MarvelApiService marvelService;

    @Autowired
    public MarvelController(MarvelApiService marvelService) {
        this.marvelService = marvelService;
    }

    @GetMapping
    public List<MarvelCharacter> getAllCharacters() {
        return marvelService.getAllCharactersFromApi();
    }

    @GetMapping("/{characterId}")
    public MarvelCharacter getCharacterById(@PathVariable Long characterId) {
        return marvelService.getCharacterByIdFromApi(characterId);
    }

    // Puedes agregar más métodos según tus necesidades
}