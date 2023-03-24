package com.aziendaVisibile.gestionale.repositories;

import com.aziendaVisibile.gestionale.models.Dipendente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente,Long> {
    Dipendente findByEmail(String email);
}
