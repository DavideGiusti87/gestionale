package com.aziendaVisibile.gestionale.controllers;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.dto.RuoloDTO;
import com.aziendaVisibile.gestionale.services.DipendenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/dipendente")
@RequiredArgsConstructor
@Slf4j
public class DipendenteController {

    @Autowired
    private DipendenteService dipendenteService;

    @GetMapping
    public ResponseEntity<List<Dipendente>> findAll() {
        return ResponseEntity.ok().body(dipendenteService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Dipendente> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(dipendenteService.findByEmail(email));
    }

    @PostMapping("/nuovo")
    public ResponseEntity<Dipendente> save(@RequestBody Dipendente dipendente) {
        Dipendente salvaDipendente = dipendenteService.salva(dipendente);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().path("/{email}")
                .buildAndExpand(salvaDipendente.getEmail()).toUriString());
        return ResponseEntity.created(uri).build();
    }



    @PostMapping("/{email}/aggiungi_ruolo")
    public ResponseEntity<?> aggiungiRuoloADipendente(@PathVariable String email, @RequestBody RuoloDTO request) {
        Dipendente dipendente = dipendenteService.aggiungiRuoloADipendente(email, request.getNomeRuolo());
        return ResponseEntity.ok(dipendente);
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
