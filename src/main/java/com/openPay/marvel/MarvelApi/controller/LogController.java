package com.openPay.marvel.MarvelApi.controller;


import com.openPay.marvel.MarvelApi.model.Log;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogRepository logRepository;

    @Autowired
    public LogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping
    public List<Log> getAllLogs() {
        return logRepository.findAllByOrderByIdDesc();
    }
}