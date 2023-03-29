package com.aziendaVisibile.gestionale.repositories;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.dto.IDipendenteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente,Long> {
    Dipendente findByEmail(String email);

    @Query(value = "SELECT cognome, comune, email, nome, paese, provincia FROM gestionale.dipendenti where email = :email",nativeQuery = true)
    IDipendenteDTO findByEmail4DipendenteDTO(@Param(value = "email") String email);

    Optional<Dipendente> findByNome(String nome);

    @Query(value = "SELECT cognome, comune, email, nome, paese, provincia FROM gestionale.dipendenti WHERE nome = :nome",nativeQuery = true)
    IDipendenteDTO findByNome4DipendenteDTO(@Param(value = "nome") String nome);

    Optional<Dipendente> findByCognome(String cognome);

    @Query(value = "SELECT cognome, comune, email, nome, paese, provincia FROM gestionale.dipendenti where cognome = :cognome",nativeQuery = true)
    IDipendenteDTO findByCognome4DipendenteDTO(@Param(value = "cognome") String cognome);

    @Query(value = "SELECT cognome, comune, email, nome, paese, provincia FROM `gestionale`.`dipendenti`", nativeQuery = true)
    List<IDipendenteDTO> findAll4DipendenteDTO();
}
