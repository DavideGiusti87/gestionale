package com.aziendaVisibile.gestionale.services;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.Ruolo;
import com.aziendaVisibile.gestionale.repositories.DipendenteRepository;
import com.aziendaVisibile.gestionale.repositories.RuoloRepository;
import com.aziendaVisibile.gestionale.utilities.JwtUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DipendenteService implements UserDetailsService {
    private static final String MESSAGGIO_MAIL_NON_TROVATA = "La mail %s non è stata trovata";

    @Autowired
    private  DipendenteRepository dipendenteRepository;
    @Autowired
    private RuoloRepository ruoloRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //cerca l'utente con username in input, sul DB
        Dipendente dipendente = dipendenteRepository.findByEmail(email);
        if(dipendente == null) { //Se non esiste l'utente, viene lanciata una eccezione di tipo UsernameNotFoundException.
            String messaggio = String.format(MESSAGGIO_MAIL_NON_TROVATA, email);
            log.error(messaggio);
            throw new UsernameNotFoundException(messaggio);
        } else {
            log.debug("Trovata email nel database: {}", email);
    /*
    trasforma i ruoli RoleEntity in SimpleGrantedAuthority,
    che è la classe di default di Spring Security per la gestione dei ruoli
    */
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            dipendente.getRuolo().forEach(ruolo -> {
                authorities.add(new SimpleGrantedAuthority(ruolo.getNome()));
            });
            //Restituisce una istanza di tipo User, che è una classe di Spring Security che implementa UserDetails
            return new User(dipendente.getEmail(), dipendente.getPassword(), authorities);
        }

    }

    public Dipendente salva(Dipendente dipendente) {
        log.info("Salvataggio dell'utente con email {} nel database", dipendente.getEmail());
        //Codifica la password prima di salvare a db.
        dipendente.setPassword(passwordEncoder.encode(dipendente.getPassword()));
        dipendenteRepository.save(dipendente);
        this.aggiungiRuoloADipendente(dipendente.getEmail(), "ROLE_USER");
        return dipendente;
    }

    public Dipendente aggiungiRuoloADipendente(String email, String nomeRuolo) {
        log.info("Aggiunta del ruolo {} all'utente {}", nomeRuolo, email);
        Dipendente dipendente = dipendenteRepository.findByEmail(email);
        if(!dipendente.getRuolo().isEmpty()){
            dipendente.getRuolo().clear();
        }
        Ruolo ruolo = ruoloRepository.findByNome(nomeRuolo);
        dipendente.getRuolo().add(ruolo);
        return dipendente;
    }

    @Transactional(readOnly = true)
    public Dipendente findByEmail(String email) {
        log.info("Recupero dell'utente con email {}", email);
        return dipendenteRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Dipendente> findAll() {
        log.info("Recupero di tutti gli utenti");
        return dipendenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String,String> refreshToken(String autorizzazioneHeader, String emettitore) throws BadJOSEException,
            ParseException, JOSEException {

        String refreshToken = autorizzazioneHeader.substring("Bearer ".length());
        UsernamePasswordAuthenticationToken authenticationToken = JwtUtil.parseToken(refreshToken);
        String email = authenticationToken.getName();
        Dipendente dipendente = findByEmail(email);
        List<String> ruoli = dipendente.getRuolo().stream().map(Ruolo::getNome).collect(Collectors.toList());
        String accessToken = JwtUtil.createAccessToken(email, emettitore, ruoli);
        return Map.of("access_token", accessToken, "refresh_token", refreshToken);
    }
}
