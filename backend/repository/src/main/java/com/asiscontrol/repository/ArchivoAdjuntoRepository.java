package com.asiscontrol.repository;

import com.asiscontrol.entity.ArchivoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArchivoAdjuntoRepository extends JpaRepository<ArchivoAdjunto, Long> {

    Optional<ArchivoAdjunto> findByIdAndActivoTrue(Long id);

    List<ArchivoAdjunto> findAllByIdInAndActivoTrue(Collection<Long> ids);
}
