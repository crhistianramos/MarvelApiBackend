package com.openPay.marvel.MarvelApi.repository;

 import com.openPay.marvel.MarvelApi.model.MarvelCharacter;
 import org.springframework.data.jpa.repository.JpaRepository;

public interface MarvelCharacterRepository extends JpaRepository<MarvelCharacter, Long> {
}