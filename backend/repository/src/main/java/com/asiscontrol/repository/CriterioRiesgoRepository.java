package com.asiscontrol.repository;

import com.asiscontrol.entity.CriterioRiesgo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CriterioRiesgoRepository extends JpaRepository<CriterioRiesgo, Long> {

    Optional<CriterioRiesgo> findByIdAndActivoTrue(Long id);

    List<CriterioRiesgo> findAllByActivoTrue();

    @Query("""
            SELECT c FROM CriterioRiesgo c
            WHERE c.activo = true
              AND (:busqueda IS NULL
                   OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                   OR LOWER(COALESCE(c.descripcion, '')) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            """)
    Page<CriterioRiesgo> buscar(@Param("busqueda") String busqueda, Pageable pageable);
}
