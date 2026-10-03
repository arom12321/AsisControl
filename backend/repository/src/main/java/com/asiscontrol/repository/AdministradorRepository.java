package com.asiscontrol.repository;

import com.asiscontrol.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByIdAndActivoTrue(Long id);

    boolean existsByPersonaId(Long personaId);

    Optional<Administrador> findByPersonaIdAndActivoTrue(Long personaId);
}
