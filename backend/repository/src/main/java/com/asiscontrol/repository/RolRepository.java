package com.asiscontrol.repository;

import com.asiscontrol.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByIdAndActivoTrue(Long id);

    Optional<Rol> findByNombreIgnoreCase(String nombre);

    List<Rol> findAllByActivoTrueOrderByNombreAsc();
}
