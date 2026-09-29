package com.asiscontrol.security;

import com.asiscontrol.entity.enums.EstadoSesion;
import com.asiscontrol.repository.SesionAccesoRepository;
import com.asiscontrol.util.HashUtils;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

public class SessionTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_SESSION = new OAuth2Error(
            "invalid_token",
            "La sesion fue revocada o expiro",
            null
    );

    private final SesionAccesoRepository sesionRepository;

    public SessionTokenValidator(SesionAccesoRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return sesionRepository.findByTokenHashAndEstado(
                        HashUtils.sha256(token.getTokenValue()),
                        EstadoSesion.VIGENTE
                )
                .filter(session -> session.getFechaExpiracion().isAfter(Instant.now()))
                .map(session -> OAuth2TokenValidatorResult.success())
                .orElseGet(() -> OAuth2TokenValidatorResult.failure(INVALID_SESSION));
    }
}
