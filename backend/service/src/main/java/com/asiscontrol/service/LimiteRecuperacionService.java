package com.asiscontrol.service;

import com.asiscontrol.entity.LimiteRecuperacion;
import com.asiscontrol.repository.LimiteRecuperacionRepository;
import com.asiscontrol.util.HashUtils;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.*;

@Service
public class LimiteRecuperacionService {
    private final LimiteRecuperacionRepository repo;
    private final TransactionTemplate tx;

    public LimiteRecuperacionService(
            LimiteRecuperacionRepository repo, PlatformTransactionManager manager) {
        this.repo = repo;
        tx = new TransactionTemplate(manager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public boolean permitir(String identifier, String ip) {
        return consumir("ip:" + ip) && consumir("usuario:" + identifier);
    }

    private boolean consumir(String value) {
        String key = HashUtils.sha256(value);
        for (int intento = 0; intento < 3; intento++) {
            try {
                return Boolean.TRUE.equals(
                        tx.execute(
                                status -> {
                                    Instant now = Instant.now();
                                    LimiteRecuperacion l =
                                            repo.bloquear(key)
                                                    .orElseGet(
                                                            () -> {
                                                                LimiteRecuperacion nuevo =
                                                                        new LimiteRecuperacion();
                                                                nuevo.setClave(key);
                                                                nuevo.setInicio(now);
                                                                return nuevo;
                                                            });
                                    if (!l.getInicio().plus(Duration.ofHours(1)).isAfter(now)) {
                                        l.setInicio(now);
                                        l.setCantidad(0);
                                    }
                                    if (l.getCantidad() >= 3) return false;
                                    l.setCantidad(l.getCantidad() + 1);
                                    repo.saveAndFlush(l);
                                    return true;
                                }));
            } catch (DataIntegrityViolationException | ConcurrencyFailureException collision) {
                if (intento == 2) return false;
            }
        }
        return false;
    }
}
