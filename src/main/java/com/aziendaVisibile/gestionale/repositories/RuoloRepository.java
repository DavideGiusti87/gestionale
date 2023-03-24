package com.aziendaVisibile.gestionale.repositories;

import com.aziendaVisibile.gestionale.models.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo,Long> {
    Ruolo findByNome(String nome);
}
