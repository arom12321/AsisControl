package com.asiscontrol.security;

import com.asiscontrol.util.HashUtils;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class SessionTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_SESSION =
            new OAuth2Error("invalid_token", "La sesion fue revocada o expiro", null);

    private final com.asiscontrol.service.SesionActividadService actividad;

    public SessionTokenValidator(com.asiscontrol.service.SesionActividadService actividad) {
        this.actividad = actividad;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return actividad.validar(HashUtils.sha256(token.getTokenValue()))
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(INVALID_SESSION);
    }
}
