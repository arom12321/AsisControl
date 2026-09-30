package com.asiscontrol.repository;

import com.asiscontrol.entity.Persona;
import com.asiscontrol.entity.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {

    Optional<Persona> findByIdAndActivoTrue(Long id);

    Optional<Persona> findByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento,
            String numeroDocumento
    );

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Persona> findByCorreoIgnoreCase(String correo);
}
