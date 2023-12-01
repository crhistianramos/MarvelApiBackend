package com.openPay.marvel.MarvelApi.controller;


import com.openPay.marvel.MarvelApi.model.AuthenticationReq;
import com.openPay.marvel.MarvelApi.model.Log;
import com.openPay.marvel.MarvelApi.model.TokenInfo;
import com.openPay.marvel.MarvelApi.repository.LogRepository;
import com.openPay.marvel.MarvelApi.service.JwtUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
public class LogController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService usuarioDetailsService;

    @Autowired
    private JwtUtilService jwtUtilService;

    private final LogRepository logRepository;

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    public LogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping
    public List<Log> getAllLogs() {
        return this.logRepository.findAllByOrderByIdDesc();
    }

}