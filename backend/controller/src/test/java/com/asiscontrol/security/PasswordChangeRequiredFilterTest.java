package com.asiscontrol.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordChangeRequiredFilterTest {

    private final PasswordChangeRequiredFilter filter =
            new PasswordChangeRequiredFilter();

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void bloqueaModulosMientrasLaContrasenaSeaTemporal() throws Exception {
        autenticar(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/dashboard");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean cadenaInvocada = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> cadenaInvocada.set(true));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("CAMBIO_CONTRASENA_REQUERIDO");
        assertThat(cadenaInvocada).isFalse();
    }

    @Test
    void permiteCambiarLaContrasenaTemporal() throws Exception {
        autenticar(true);
        MockHttpServletRequest request =
                new MockHttpServletRequest("POST", "/api/auth/change-password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean cadenaInvocada = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> cadenaInvocada.set(true));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(cadenaInvocada).isTrue();
    }

    private void autenticar(boolean requiereCambio) {
        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("token-prueba")
                .header("alg", "none")
                .subject("usuario-prueba")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .claim("requiereCambioContrasena", requiereCambio)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        ));
    }
}
