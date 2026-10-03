package com.asiscontrol.dto.notificacion;

import com.asiscontrol.entity.enums.TipoNotificacion;

import java.time.Instant;

public final class NotificacionDtos {

    private NotificacionDtos() {
    }

    public record Response(
            Long id,
            String titulo,
            String mensaje,
            TipoNotificacion tipo,
            String urlDestino,
            boolean leida,
            Instant fechaLectura,
            Instant fechaCreacion
    ) {
    }

    public record Summary(long noLeidas) {
    }
}
