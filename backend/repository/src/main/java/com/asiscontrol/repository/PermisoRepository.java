package com.asiscontrol.repository;

import com.asiscontrol.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByCodigoIgnoreCase(String codigo);

    List<Permiso> findAllByActivoTrueOrderByCodigoAsc();
}
