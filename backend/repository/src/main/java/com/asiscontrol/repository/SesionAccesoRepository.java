package com.asiscontrol.repository;

import com.asiscontrol.entity.SesionAcceso;
import com.asiscontrol.entity.enums.EstadoSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SesionAccesoRepository extends JpaRepository<SesionAcceso, Long> {

    Optional<SesionAcceso> findByTokenHashAndEstado(String tokenHash, EstadoSesion estado);

    @Modifying
    @Query("""
            update SesionAcceso s
               set s.estado = :estado, s.fechaRevocacion = :fecha
             where s.usuario.id = :usuarioId and s.estado = com.asiscontrol.entity.enums.EstadoSesion.VIGENTE
            """)
    int revokeAllByUsuarioId(
            @Param("usuarioId") Long usuarioId,
            @Param("estado") EstadoSesion estado,
            @Param("fecha") Instant fecha
    );
}
