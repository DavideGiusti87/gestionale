package com.aziendaVisibile.gestionale.controllers;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.dto.LoginDTO;
import com.aziendaVisibile.gestionale.models.dto.RuoloDTO;
import com.aziendaVisibile.gestionale.services.DipendenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/new_login")
@Slf4j
public class LoginController {

    @Autowired
    private DipendenteService dipendenteService;

    @PostMapping("/nuovo")
    public ResponseEntity<Dipendente> save(@RequestBody LoginDTO dipendente) {
        Dipendente salvaDipendente = dipendenteService.salva(dipendente);
        return ResponseEntity.ok(salvaDipendente);
    }


    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                Map<String, String> tokenMap = dipendenteService.refreshToken(authorizationHeader, request.getRequestURL().toString());
                response.addHeader("access_token", tokenMap.get("access_token"));
                response.addHeader("refresh_token", tokenMap.get("refresh_token"));
            }
            catch (Exception e) {
                log.error(String.format("Errore di aggiornamento del token: %s", authorizationHeader), e);
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Token aggiornato mancante");
        }
    }
}
