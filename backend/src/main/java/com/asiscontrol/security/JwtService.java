package com.asiscontrol.security;

import com.asiscontrol.entity.AccesoUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.expiration-minutes:30}") long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public GeneratedToken generate(AuthenticatedUser authenticatedUser) {
        AccesoUsuario usuario = authenticatedUser.getUsuario();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expirationMinutes, ChronoUnit.MINUTES);
        List<String> authorities = authenticatedUser.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("asiscontrol-backend")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(usuario.getUsername())
                .claim("usuarioId", usuario.getId())
                .claim("personaId", usuario.getPersona().getId())
                .claim("rol", usuario.getRol().getNombre())
                .claim("requiereCambioContrasena", usuario.isRequiereCambioContrasena())
                .claim("authorities", authorities)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new GeneratedToken(value, issuedAt, expiresAt);
    }

    public record GeneratedToken(String value, Instant issuedAt, Instant expiresAt) {
    }
}
