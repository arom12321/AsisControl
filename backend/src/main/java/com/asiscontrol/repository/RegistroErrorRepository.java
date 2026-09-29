package com.asiscontrol.repository;

import com.asiscontrol.entity.RegistroError;
import com.asiscontrol.entity.enums.EstadoError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroErrorRepository extends JpaRepository<RegistroError, Long> {

    Page<RegistroError> findAllByEstadoOrderByFechaHoraDesc(EstadoError estado, Pageable pageable);
}
