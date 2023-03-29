package com.aziendaVisibile.gestionale.models.dto;

import com.aziendaVisibile.gestionale.models.Ruolo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DipendenteDTO implements Serializable {

    private String email;

    private String nome;

    private String cognome;

    private String provincia;

    private String comune;

    private String paese;

}
