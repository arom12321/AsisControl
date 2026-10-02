package com.asiscontrol.repository;

import com.asiscontrol.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Page<Notificacion> findAllByDestinatarioIdAndActivoTrue(Long usuarioId, Pageable pageable);

    Optional<Notificacion> findByIdAndDestinatarioIdAndActivoTrue(Long id, Long usuarioId);

    long countByDestinatarioIdAndLeidaFalseAndActivoTrue(Long usuarioId);

    @Modifying
    @Query("""
            update Notificacion n
               set n.leida = true, n.fechaLectura = :fecha
             where n.destinatario.id = :usuarioId and n.leida = false and n.activo = true
            """)
    int markAllRead(@Param("usuarioId") Long usuarioId, @Param("fecha") Instant fecha);
}
