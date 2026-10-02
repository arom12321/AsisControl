package com.asiscontrol.repository;

import com.asiscontrol.entity.SeguimientoError;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoErrorRepository extends JpaRepository<SeguimientoError, Long> {
    List<SeguimientoError> findByErrorIdOrderByFechaAsc(Long id);
}
