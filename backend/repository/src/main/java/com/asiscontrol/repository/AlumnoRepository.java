package com.asiscontrol.repository;

import com.asiscontrol.entity.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByIdAndActivoTrue(Long id);

    Optional<Alumno> findByCodigoAlumnoIgnoreCase(String codigoAlumno);

    Optional<Alumno> findByPersonaIdAndActivoTrue(Long personaId);

    @Query("""
            select a from Alumno a
            join a.persona p
            where a.activo = true and p.activo = true
              and (:search is null or lower(a.codigoAlumno) like lower(concat('%', :search, '%'))
                   or lower(p.nombres) like lower(concat('%', :search, '%'))
                   or lower(p.apellidoPaterno) like lower(concat('%', :search, '%'))
                   or lower(p.apellidoMaterno) like lower(concat('%', :search, '%'))
                   or lower(p.numeroDocumento) like lower(concat('%', :search, '%')))
            """)
    Page<Alumno> searchActive(@Param("search") String search, Pageable pageable);
}
