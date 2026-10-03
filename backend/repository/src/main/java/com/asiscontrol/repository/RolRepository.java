package com.asiscontrol.repository;

import com.asiscontrol.entity.Rol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByIdAndActivoTrue(Long id);

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "select r from Rol r where r.nombre='ADMINISTRADOR'")
    Optional<Rol> bloquearAdministrador();

    Optional<Rol> findByNombreIgnoreCase(String nombre);

    List<Rol> findAllByActivoTrueOrderByNombreAsc();
}
