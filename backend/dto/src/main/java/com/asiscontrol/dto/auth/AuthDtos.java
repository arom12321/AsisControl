package com.asiscontrol.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "El usuario o correo es obligatorio")
            String identifier,
            @NotBlank(message = "La contrasena es obligatoria")
            String password
    ) {
    }

    public record AuthResponse(
            String tokenType,
            String accessToken,
            Instant expiresAt,
            UserSummary user
    ) {
    }

    public record UserSummary(
            Long id,
            Long personaId,
            String username,
            String correo,
            String nombreCompleto,
            String rol,
            List<String> permisos,
            boolean requiereCambioContrasena
    ) {
    }

    public record ChangePasswordRequest(
            @NotBlank(message = "La contrasena actual es obligatoria")
            String currentPassword,
            @NotBlank(message = "La nueva contrasena es obligatoria")
            @Size(min = 10, max = 72, message = "La nueva contrasena debe tener entre 10 y 72 caracteres")
            String newPassword
    ) {
    }

    public record MessageResponse(String message) {
    }
}
