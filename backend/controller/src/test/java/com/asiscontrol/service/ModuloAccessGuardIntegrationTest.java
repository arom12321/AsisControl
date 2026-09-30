package com.asiscontrol.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ModuloAccessGuardIntegrationTest {

    @Autowired
    private ModuloAccessGuard accessGuard;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void niegaRecursosAjenosAlApoderadoSinVinculos() {
        autenticar("APODERADO");

        assertThat(accessGuard.puedeAccederAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederApoderado(1L)).isFalse();
        assertThat(accessGuard.puedeAccederCompromisoPago(1L)).isFalse();
        assertThat(accessGuard.puedeAccederTransaccionPago(1L)).isFalse();
        assertThat(accessGuard.puedeAccederComprobante(1L)).isFalse();
        assertThat(accessGuard.puedeAccederMatricula(1L)).isFalse();
        assertThat(accessGuard.puedeAccederSolicitudMatricula(1L)).isFalse();
        assertThat(accessGuard.puedeAccederTarea(1L)).isFalse();
        assertThat(accessGuard.puedeAccederAsistenciaAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederJustificacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEntrega(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEvaluacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederArchivo(1L)).isFalse();
    }

    @Test
    void niegaRecursosAjenosAlAlumno() {
        autenticar("ALUMNO");

        assertThat(accessGuard.puedeAccederAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederMatricula(1L)).isFalse();
        assertThat(accessGuard.puedeAccederSolicitudMatricula(1L)).isFalse();
        assertThat(accessGuard.puedeAccederTarea(1L)).isFalse();
        assertThat(accessGuard.puedeAccederAsistenciaAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederJustificacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEntrega(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEvaluacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederArchivo(1L)).isFalse();
    }

    @Test
    void niegaRecursosAjenosAlDocente() {
        autenticar("DOCENTE");

        assertThat(accessGuard.puedeAccederAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederDocente(1L)).isFalse();
        assertThat(accessGuard.puedeAccederTarea(1L)).isFalse();
        assertThat(accessGuard.puedeAccederAsistenciaAlumno(1L)).isFalse();
        assertThat(accessGuard.puedeAccederJustificacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEntrega(1L)).isFalse();
        assertThat(accessGuard.puedeAccederEvaluacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederCalificacion(1L)).isFalse();
        assertThat(accessGuard.puedeAccederArchivo(1L)).isFalse();
    }

    @Test
    void niegaArchivoNoContableATesoreria() {
        autenticar("TESORERIA");

        assertThat(accessGuard.puedeAccederArchivo(1L)).isFalse();
    }

    private void autenticar(String rol) {
        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("token-" + rol.toLowerCase())
                .header("alg", "none")
                .subject("usuario-prueba")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .claim("usuarioId", 100L)
                .claim("personaId", 101L)
                .build();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
