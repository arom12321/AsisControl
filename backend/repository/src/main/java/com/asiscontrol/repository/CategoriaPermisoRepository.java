package com.asiscontrol.repository;

import com.asiscontrol.entity.CategoriaPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaPermisoRepository extends JpaRepository<CategoriaPermiso, Long> {

    Optional<CategoriaPermiso> findByNombreIgnoreCase(String nombre);

    List<CategoriaPermiso> findAllByActivoTrueOrderByNombreAsc();
}
