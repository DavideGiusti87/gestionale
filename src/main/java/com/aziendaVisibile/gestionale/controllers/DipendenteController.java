package com.aziendaVisibile.gestionale.controllers;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.dto.RuoloDTO;
import com.aziendaVisibile.gestionale.services.DipendenteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/dipendente")
@Slf4j
public class DipendenteController {

    @Autowired
    private DipendenteService dipendenteService;


    @GetMapping
    public ResponseEntity findAll(Principal principal) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findAll(principal));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @GetMapping("/email")
    public ResponseEntity findByEmail(@RequestParam String email, Principal principal) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByEmail(email, principal));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/id")
    public ResponseEntity findById(@RequestParam Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findById(id));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/nome")
    public ResponseEntity findByNome(@RequestParam String nome, Principal principal){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByNome(nome, principal));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/cognome")
    public ResponseEntity findByCognome(@RequestParam String cognome, Principal principal){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByCognome(cognome, principal));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/anagrafica")
    public  ResponseEntity aggiornaAnagrafica(@RequestBody Dipendente aggiornamento, Principal principal){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.aggiornaAnagrafica(principal,aggiornamento));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{email}")
    public ResponseEntity implementaStipendio(@PathVariable String email, @RequestParam Double stipendio){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.implementaStipendio(email,stipendio));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/aggiungi_ruolo")
    public ResponseEntity<?> aggiungiRuoloADipendente(@RequestParam String email, @RequestBody RuoloDTO request) {
        Dipendente dipendente = dipendenteService.aggiungiRuoloADipendente(email, request.getNomeRuolo());
        return ResponseEntity.ok(dipendente);
    }
}
