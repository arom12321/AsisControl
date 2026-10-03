package com.asiscontrol.repository;

import com.asiscontrol.entity.Persona;
import com.asiscontrol.entity.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {

    Optional<Persona> findByIdAndActivoTrue(Long id);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Persona p where p.id = :id and p.activo = true")
    Optional<Persona> lockActiveById(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Query("""
        select p from Persona p where p.activo = true and
        (:search is null or lower(p.nombres) like lower(concat('%', :search, '%'))
        or lower(p.apellidoPaterno) like lower(concat('%', :search, '%'))
        or lower(p.apellidoMaterno) like lower(concat('%', :search, '%'))
        or lower(p.numeroDocumento) like lower(concat('%', :search, '%')))
        """)
    org.springframework.data.domain.Page<Persona> searchActive(
        @org.springframework.data.repository.query.Param("search") String search,
        org.springframework.data.domain.Pageable pageable);

    Optional<Persona> findByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento,
            String numeroDocumento
    );

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Persona> findByCorreoIgnoreCase(String correo);
}
