package com.openPay.marvel.MarvelApi.service;

import com.openPay.marvel.MarvelApi.model.MarvelCharacter;

import java.util.List;

public interface MarvelApiService {

    List<MarvelCharacter> getAllCharactersFromApi();

    MarvelCharacter getCharacterByIdFromApi(Long id);
}
