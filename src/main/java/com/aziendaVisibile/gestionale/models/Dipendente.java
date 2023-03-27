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

    @Column(name = "nome")
    @Size(min = 3, max = 30)
    private String nome;

    @Column(name = "cognome")
    @Size(min = 3, max = 30)
    private String cognome;

    @Column(name = "via")
    @Size(min = 5, max = 50)
    private String via;

    @Column(name = "provincia")
    @Size(min = 2)
    private String provincia;

    @Column(name = "comune")
    @Size(min = 4, max = 10)
    private String comune;

    @Column(name = "civico")
    @Size(min = 1, max = 10)
    private String civico;

    @Column(name = "cap")
    @Size(min = 5, max = 8 )
    private String cap;

    @Column(name = "paese")
    @Size(min = 5, max = 20)
    private String paese;

    @Column(name = "telefono")
    //@Pattern(regexp = "\\(\\+[0-9]{1,5}\\)[0-9]{3,15}", message = "Formato numero non valido!")
    private Integer telefono;

    @Column(name = "stipendio")
    private Double stipendio;
}
