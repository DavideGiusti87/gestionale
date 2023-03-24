package com.aziendaVisibile.gestionale.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "dipendenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dipendente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(name = "email", unique = true, nullable = false)
    @Size(max = 254, message = "Email può contenere fino a 254 caratteri")
    @Pattern(regexp = "[^@]+@[^\\.]+\\..+", message = "Formato Email non valido")
    private String email;

    @Column(name = "password", nullable = false)
    //@Size(min = 6, max = 8, message = "La password deve contenere minimo 6 caratteri, massimo 8 caratteri")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Ruolo> ruolo = new ArrayList<>();
}
