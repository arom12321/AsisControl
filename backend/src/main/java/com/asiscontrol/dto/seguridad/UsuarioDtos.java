package com.asiscontrol.dto.seguridad;

import com.asiscontrol.entity.enums.EstadoAcceso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record CreateRequest(
            @NotNull(message = "La persona es obligatoria")
            Long personaId,
            @NotNull(message = "El rol es obligatorio")
            Long rolId,
            @NotBlank(message = "El nombre de usuario es obligatorio")
            @Size(min = 4, max = 80)
            String username,
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato valido")
            String correo,
            @NotBlank(message = "La contrasena temporal es obligatoria")
            @Size(min = 10, max = 72)
            String password,
            boolean requiereCambioContrasena
    ) {
    }

    public record UpdateRequest(
            @NotNull(message = "El rol es obligatorio")
            Long rolId,
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato valido")
            String correo,
            @NotNull(message = "El estado es obligatorio")
            EstadoAcceso estado
    ) {
    }

    public record ResetPasswordRequest(
            @NotBlank(message = "La contrasena temporal es obligatoria")
            @Size(min = 10, max = 72)
            String temporaryPassword
    ) {
    }

    public record Response(
            Long id,
            Long personaId,
            String username,
            String correo,
            String nombreCompleto,
            Long rolId,
            String rol,
            EstadoAcceso estado,
            boolean requiereCambioContrasena,
            int intentosFallidos,
            Instant bloqueadoHasta,
            Instant ultimoAcceso,
            Instant fechaCreacion
    ) {
    }

    public record RolRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 50)
            String nombre,
            @Size(max = 250)
            String descripcion,
            Set<Long> permisoIds
    ) {
    }

    public record RolResponse(
            Long id,
            String nombre,
            String descripcion,
            List<PermisoResponse> permisos,
            boolean activo
    ) {
    }

    public record PermisoResponse(
            Long id,
            String codigo,
            String nombre,
            String descripcion,
            String categoria
    ) {
    }
}
