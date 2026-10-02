package com.asiscontrol.service;

import com.asiscontrol.repository.SesionAccesoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
public class SesionActividadService {
    private final SesionAccesoRepository repo;

    public SesionActividadService(SesionAccesoRepository r) {
        repo = r;
    }

    @Transactional
    public boolean validar(String hash) {
        Instant now = Instant.now();
        return repo.registrarActividad(hash, now, now.minus(Duration.ofMinutes(30))) == 1;
    }
}
