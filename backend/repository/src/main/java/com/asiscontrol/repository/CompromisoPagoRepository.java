package com.asiscontrol.repository;

import com.asiscontrol.entity.CompromisoPago;
import com.asiscontrol.entity.enums.EstadoCompromisoPago;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface CompromisoPagoRepository extends JpaRepository<CompromisoPago, Long> {

    Optional<CompromisoPago> findByIdAndActivoTrue(Long id);

    Optional<CompromisoPago> findByCodigoCompromisoIgnoreCaseAndActivoTrue(String codigoCompromiso);

    Optional<CompromisoPago> findByCodigoCompromisoIgnoreCase(String codigoCompromiso);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CompromisoPago c WHERE c.id = :id AND c.activo = true")
    Optional<CompromisoPago> bloquearPorId(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE CompromisoPago c
            SET c.estado = com.asiscontrol.entity.enums.EstadoCompromisoPago.VENCIDO
            WHERE c.activo = true
              AND c.fechaVencimiento < :fecha
              AND c.estado IN :estados
            """)
    int marcarVencidos(
            @Param("fecha") LocalDate fecha,
            @Param("estados") Collection<EstadoCompromisoPago> estados
    );

    @Query("""
            SELECT c FROM CompromisoPago c
            WHERE c.activo = true
              AND (:estado IS NULL OR c.estado = :estado)
              AND (:idMatricula IS NULL OR c.matricula.id = :idMatricula)
              AND (:personaApoderadoId IS NULL OR EXISTS (
                    SELECT v.id FROM AlumnoApoderado v
                    WHERE v.activo = true
                      AND v.alumno.id = c.matricula.alumno.id
                      AND v.apoderado.persona.id = :personaApoderadoId
                  ))
              AND (:busqueda IS NULL
                   OR LOWER(c.codigoCompromiso) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                   OR LOWER(c.concepto) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            """)
    Page<CompromisoPago> buscar(
            @Param("estado") EstadoCompromisoPago estado,
            @Param("idMatricula") Long idMatricula,
            @Param("busqueda") String busqueda,
            @Param("personaApoderadoId") Long personaApoderadoId,
            Pageable pageable
    );
}
