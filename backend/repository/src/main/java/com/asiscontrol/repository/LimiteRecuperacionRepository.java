package com.asiscontrol.repository;

import com.asiscontrol.entity.LimiteRecuperacion;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface LimiteRecuperacionRepository extends JpaRepository<LimiteRecuperacion, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LimiteRecuperacion l where l.clave=:clave")
    Optional<LimiteRecuperacion> bloquear(@Param("clave") String clave);

    @Modifying
    @Query("delete from LimiteRecuperacion l where l.inicio<:antes")
    int limpiar(@Param("antes") Instant antes);
}
