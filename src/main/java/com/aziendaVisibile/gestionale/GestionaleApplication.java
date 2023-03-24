package com.aziendaVisibile.gestionale;

import com.aziendaVisibile.gestionale.models.Dipendente;
import com.aziendaVisibile.gestionale.models.Ruolo;
import com.aziendaVisibile.gestionale.services.DipendenteService;
import com.aziendaVisibile.gestionale.services.RuoloService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class GestionaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionaleApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(DipendenteService dipendenteService, RuoloService ruoloService) {
		if(ruoloService.findAll().isEmpty()) {
			return args -> {
				ruoloService.salva(new Ruolo(null, "ROLE_USER"));
				ruoloService.salva(new Ruolo(null, "ROLE_ADMIN"));

				dipendenteService.salva(new Dipendente(null, "user@root.com", "root000", new ArrayList<>()));

				dipendenteService.aggiungiRuoloADipendente("user@root.com", "ROLE_ADMIN");
			};
		}else return null;
	}

}
