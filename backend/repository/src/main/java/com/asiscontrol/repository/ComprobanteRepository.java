package com.asiscontrol.repository;

import com.asiscontrol.entity.Comprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Optional<Comprobante> findByIdAndActivoTrue(Long id);

    Optional<Comprobante> findByTransaccionPagoIdAndActivoTrue(Long idTransaccionPago);

    boolean existsBySerieIgnoreCaseAndNumeroIgnoreCaseAndActivoTrue(String serie, String numero);

    Page<Comprobante> findAllByActivoTrue(Pageable pageable);

    @Query("""
            SELECT c FROM Comprobante c
            WHERE c.activo = true
              AND (:personaApoderadoId IS NULL OR EXISTS (
                    SELECT v.id FROM AlumnoApoderado v
                    WHERE v.activo = true
                      AND v.alumno.id = c.transaccionPago.compromisoPago.matricula.alumno.id
                      AND v.apoderado.persona.id = :personaApoderadoId
                  ))
            """)
    Page<Comprobante> buscarVisibles(
            @Param("personaApoderadoId") Long personaApoderadoId,
            Pageable pageable
    );
}
