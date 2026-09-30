package com.asiscontrol.dto.dashboard;

import java.time.Instant;
import java.util.List;

public final class DashboardDtos {

    private DashboardDtos() {
    }

    public record IndicadorResponse(
            String codigo,
            String etiqueta,
            long valor,
            String tono,
            String rutaDestino
    ) {
    }

    public record DashboardResponse(
            String rol,
            Long personaId,
            Instant generadoEn,
            List<IndicadorResponse> indicadores
    ) {
    }
}
