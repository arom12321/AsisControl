package com.asiscontrol.config;

import com.asiscontrol.repository.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Configuration
@EnableScheduling
public class LimpiezaSeguridad {
    private final LimiteRecuperacionRepository limites;
    private final TokenRecuperacionRepository tokens;

    public LimpiezaSeguridad(LimiteRecuperacionRepository l, TokenRecuperacionRepository t) {
        limites = l;
        tokens = t;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limpiar() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(1));
        limites.limpiar(cutoff);
        tokens.limpiar(cutoff);
    }
}
