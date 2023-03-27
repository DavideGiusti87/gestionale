package com.aziendaVisibile.gestionale.services;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.Ruolo;
import com.aziendaVisibile.gestionale.models.dto.LoginDTO;
import com.aziendaVisibile.gestionale.repositories.DipendenteRepository;
import com.aziendaVisibile.gestionale.repositories.RuoloRepository;
import com.aziendaVisibile.gestionale.utilities.JwtUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

import java.security.Principal;
import java.text.ParseException;
import java.util.*;
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

    @SneakyThrows
    public Dipendente salva(LoginDTO loginDTO) {
        log.info("Salvataggio dell'utente con email {} nel database", loginDTO.getEmail());
        Dipendente dipendenteDB = dipendenteRepository.findByEmail(loginDTO.getEmail());
        if(dipendenteDB != null) throw new Exception("Il dipendenteDTO è già esistente");
        Dipendente dipendente = new Dipendente();
        dipendente.setEmail(loginDTO.getEmail());
        //Codifica la password prima di salvare a db.
        dipendente.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        dipendenteRepository.save(dipendente);
        this.aggiungiRuoloADipendente(loginDTO.getEmail(), "ROLE_USER");
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
        Dipendente dipendente = dipendenteRepository.findByEmail(email);
        if (dipendente != null){
            log.info("Utente trovato");
            return dipendente;
        }else{
            log.info("Utente non trovato");
            return null;
        }

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

    @Transactional(readOnly = true)
    public Optional<Dipendente> findById(Long id) {
        log.info("Recupero dell'utente con id {}", id);
        return dipendenteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Dipendente> findByNome(String nome) {
        log.info("Recupero dell'utente con nome {}", nome);
        return dipendenteRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public Optional<Dipendente> findByCognome(String cognome) {
        log.info("Recupero dell'utente con cognome {}", cognome);
        return dipendenteRepository.findByCognome(cognome);
    }

    public Dipendente aggiornaAnagrafica(Principal principal, Dipendente aggiornamento) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        Dipendente dipendente = dipendenteRepository.findByEmail(authenticationToken.getName());
        log.info("Recuperato l'utente con email {}", dipendente.getEmail());
        dipendente.setNome(aggiornamento.getNome());
        dipendente.setCognome(aggiornamento.getCognome());
        dipendente.setEmail(aggiornamento.getEmail());
        dipendente.setCap(aggiornamento.getCap());
        dipendente.setCivico(aggiornamento.getCivico());
        dipendente.setVia(aggiornamento.getVia());
        dipendente.setComune(aggiornamento.getComune());
        dipendente.setProvincia(aggiornamento.getProvincia());
        dipendente.setPaese(aggiornamento.getPaese());
        dipendente.setTelefono(aggiornamento.getTelefono());
        log.info("Modifica del dipendente effettuata");
        return dipendente;
    }

    public Dipendente implementaStipendio(String email, Double stipendio) {
        Dipendente dipendente = dipendenteRepository.findByEmail(email);
        dipendente.setStipendio(stipendio);
        log.info("Modificato stipendio del dipendente {}", dipendente);
        return dipendente;
    }
}
