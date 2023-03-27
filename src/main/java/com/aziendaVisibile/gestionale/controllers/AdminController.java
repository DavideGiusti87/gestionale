package com.aziendaVisibile.gestionale.controllers;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.services.DipendenteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private DipendenteService dipendenteService;

    @GetMapping
    public ResponseEntity findAll() {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findAll());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/email{email}")
    public ResponseEntity findByEmail(@PathVariable String email) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByEmail(email));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/id{id}")
    public ResponseEntity findById(@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findById(id));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/nome{nome}")
    public ResponseEntity findByNome(@PathVariable String nome){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByNome(nome));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/cognome{cognome}")
    public ResponseEntity findByCognome(@PathVariable String cognome){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(dipendenteService.findByCognome(cognome));
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
}
