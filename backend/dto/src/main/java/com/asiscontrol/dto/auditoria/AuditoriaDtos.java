package com.asiscontrol.dto.auditoria;

import com.asiscontrol.entity.enums.EstadoError;
import com.asiscontrol.entity.enums.ResultadoAuditoria;
import com.asiscontrol.entity.enums.SeveridadError;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class AuditoriaDtos {

    private AuditoriaDtos() {
    }

    public record AuditResponse(
            Long id,
            String actorIdentificador,
            String rolActor,
            String accion,
            String modulo,
            String entidad,
            String registroId,
            Instant fechaHora,
            ResultadoAuditoria resultado,
            String motivo,
            String correlationId
    ) {
    }

    public record ErrorResponse(
            Long id,
            Instant fechaHora,
            SeveridadError severidad,
            String componente,
            String mensajeSeguro,
            String correlationId,
            EstadoError estado,
            String observacionSeguimiento
    ) {
    }

    public record UpdateErrorRequest(
            @NotNull EstadoError estado,
            @Size(max = 1000) String observacionSeguimiento
    ) {
    }
}
