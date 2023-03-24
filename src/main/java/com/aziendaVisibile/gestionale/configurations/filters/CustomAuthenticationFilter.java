package com.aziendaVisibile.gestionale.configurations.filters;

import com.aziendaVisibile.gestionale.utilities.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String ERRORE_MESSAGGIO_CREDENZIALI = "Autenticazione non riuscita per email utente: %s e password: %s";

    private final AuthenticationManager AUTHENTICATION_MANAGER;

    /*
    invocato nella fase di login
    prende username e password dalla RequestBody e richiama authenticationManager.authenticate,
    che a sua volta chiama UserDetailService per controllare che lo user sia presente nel database,
    e poi controlla che la password decodificata dell'istanza User (creata da UserDetailService)
    corrisponda a quella data in input.
    Se i check sono superati, viene richiamato il metodo successfulAuthentication, altrimenti unsuccessfulAuthentication.
    */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String email = null;
        String password = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(request.getInputStream(), Map.class);
            email = map.get("email");
            password = map.get("password");
            log.debug("Accesso con e-mail: {}", email);
            return AUTHENTICATION_MANAGER.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        }
        catch (AuthenticationException e) {
            log.error(String.format(ERRORE_MESSAGGIO_CREDENZIALI, email, password), e);
            throw e;
        }
        catch (Exception e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
            Map<String, String> error = new HashMap<>();
            error.put("errorMessage", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            throw new RuntimeException(String.format("Errore in attemptAuthentication con email %s e password %s", email, password), e);
        }
    }

    //crea l'access token e il refresh token e li aggiunge all'header di risposta della chiamata /login.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        User user = (User)authentication.getPrincipal();
        String accessToken = JwtUtil.createAccessToken(user.getUsername(), request.getRequestURL().toString(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String refreshToken = JwtUtil.createRefreshToken(user.getUsername());
        response.addHeader("access_token", accessToken);
        response.addHeader("refresh_token", refreshToken);
    }

    /*
    Quando attemptAuthentication lancia una eccezione di tipo AuthenticationException viene invocato questo metodo.
    La sovrascrittura di questo metodo, per i nostri scopi, è opzionale.
    Noi lo utilizziamo per restituire 401 e un messaggio di errore nella Response Body.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", "Bad credentials");
        response.setContentType(APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), error);
    }
}
