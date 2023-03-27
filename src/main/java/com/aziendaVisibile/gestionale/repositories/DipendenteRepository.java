package com.aziendaVisibile.gestionale.repositories;

import com.aziendaVisibile.gestionale.models.Dipendente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente,Long> {
    Dipendente findByEmail(String email);

    Optional<Dipendente> findByNome(String nome);

    Optional<Dipendente> findByCognome(String cognome);
}
