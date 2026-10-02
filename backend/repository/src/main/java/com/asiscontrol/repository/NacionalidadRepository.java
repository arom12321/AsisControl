package com.asiscontrol.repository;

import com.asiscontrol.entity.Nacionalidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NacionalidadRepository extends JpaRepository<Nacionalidad, Long> {

    Optional<Nacionalidad> findByIdAndActivoTrue(Long id);

    Optional<Nacionalidad> findByNombreIgnoreCase(String nombre);

    List<Nacionalidad> findAllByActivoTrueOrderByNombreAsc();
}
