package com.aziendaVisibile.gestionale.services;

import com.aziendaVisibile.gestionale.models.Ruolo;
import com.aziendaVisibile.gestionale.repositories.RuoloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuoloService {

    @Autowired
    private RuoloRepository ruoloRepository;

    public Ruolo salva(Ruolo ruolo){
        log.info("Salvataggio del ruolo {} nel database",ruolo.getNome());
        return ruoloRepository.save(ruolo);
    }

    public List<Ruolo> findAll(){
        return ruoloRepository.findAll();
    }
}
