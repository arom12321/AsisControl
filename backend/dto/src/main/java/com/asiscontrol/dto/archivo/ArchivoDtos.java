package com.asiscontrol.dto.archivo;

import java.time.Instant;

public final class ArchivoDtos {

    private ArchivoDtos() {
    }

    public record ArchivoResponse(
            Long id,
            String nombreOriginal,
            String tipoMime,
            long tamanioBytes,
            String sha256,
            Long cargadoPorPersonaId,
            Instant fechaSubida
    ) {
    }
}
