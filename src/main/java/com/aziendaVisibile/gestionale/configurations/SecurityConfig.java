package com.aziendaVisibile.gestionale.configurations;

import com.aziendaVisibile.gestionale.configurations.filters.CustomAuthenticationFilter;
import com.aziendaVisibile.gestionale.configurations.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    //sostituisce il metodo configure(AuthenticationManagerBuilder auth)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //sostituisce il metodo configure(HttpSecurity http)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        /*
        disabilitiamo il controllo di default sugli attacchi CSRF e
        indichiamo a Spring Security che non deve creare una sessione per gli utenti che si autenticano (policy STATELESS).
         */
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((auth) -> auth
                        //indichiamo a Spring Security che chiunque può consumare l'API /login e /dipendente/nuovo con verbo POST.
                        .requestMatchers(HttpMethod.POST, "/login/**","/new_login/**").permitAll()
                        //indichiamo a Spring Security che solo gli utenti con ruolo ADMIN possono consumare l'API /id/** e /aggiungi_ruolo/**.
                        .requestMatchers("/dipendente/id/**","/dipendente/aggiungi_ruolo/**").hasAuthority("ROLE_ADMIN")
                        //indichiamo a Spring Security che solo gli utenti con ruolo ADMIN possono consumare l'API /dipendente/**.con verbo PATCH
                        .requestMatchers(HttpMethod.PATCH,"/dipendente/**").hasAuthority("ROLE_ADMIN")
                        //indichiamo che tutte le altre richieste possono essere consumate se l'utente è autenticato.
                        .anyRequest().authenticated()
                )
                /*
                aggiungiamo un filtro custom per la fase di autenticazione;
                la classe custom estende la classe UsernamePasswordAuthenticationFilter di Spring Security,
                quindi, viene utilizzata solo nella fase di login
                */
                .addFilter(new CustomAuthenticationFilter(authenticationManager))
                /*
                creiamo un filtro che viene utilizzato per ogni richiesta HTTP,
                prima del filtro di tipo UsernamePasswordAuthenticationFilter,
                ovvero viene richiamato prima della classe CustomAuthenticationFilter.
                */
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers().cacheControl();

        return http.build();
    }
}
