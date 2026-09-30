package com.asiscontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class CompetenciaDto {

    private CompetenciaDto() {
    }

    public record Request(
            @NotBlank @Size(max = 30) String codigo,
            @NotBlank @Size(max = 150) String nombre,
            @Size(max = 500) String descripcion
    ) {
    }

    public record Response(
            Long id,
            String codigo,
            String nombre,
            String descripcion,
            boolean activo,
            Instant fechaCreacion,
            Instant fechaActualizacion
    ) {
    }
}
