package com.aziendaVisibile.gestionale.models.dto;

import com.aziendaVisibile.gestionale.models.Dipendente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DipendenteDTO {

    private String email;
    private String password;
}
