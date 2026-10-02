package com.asiscontrol.repository;

import com.asiscontrol.entity.AccesoUsuario;
import com.asiscontrol.entity.enums.EstadoAcceso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccesoUsuarioRepository extends JpaRepository<AccesoUsuario, Long> {

    Optional<AccesoUsuario> findByIdAndActivoTrue(Long id);

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from AccesoUsuario u where u.id=:id")
    Optional<AccesoUsuario> bloquear(@Param("id") Long id);

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query(
            "select u from AccesoUsuario u where u.activo=true and"
                + " (lower(u.username)=lower(:identifier) or lower(u.correo)=lower(:identifier))")
    Optional<AccesoUsuario> bloquearPorIdentificador(@Param("identifier") String identifier);

    long countByRolNombreAndActivoTrueAndEstado(String rol, EstadoAcceso estado);

    java.util.List<AccesoUsuario> findAllByPersonaIdAndActivoTrue(Long personaId);

    boolean existsByPersonaIdAndRolId(Long personaId, Long rolId);

    @Query(
            """
            select u from AccesoUsuario u
            where u.activo = true and (lower(u.username) = lower(:identifier)
                or lower(u.correo) = lower(:identifier))
            """)
    Optional<AccesoUsuario> findActiveByIdentifier(@Param("identifier") String identifier);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByRolIdAndActivoTrue(Long rolId);

    @Query(
            """
            select u from AccesoUsuario u
            join u.persona p
            join u.rol r
            where u.activo = true
              and (:estado is null or u.estado = :estado)
              and (:roleId is null or r.id = :roleId)
              and (:search is null or lower(u.username) like lower(concat('%', :search, '%'))
                   or lower(u.correo) like lower(concat('%', :search, '%'))
                   or lower(p.nombres) like lower(concat('%', :search, '%'))
                   or lower(p.apellidoPaterno) like lower(concat('%', :search, '%')))
            """)
    Page<AccesoUsuario> searchActive(
            @Param("search") String search,
            @Param("estado") EstadoAcceso estado,
            @Param("roleId") Long roleId,
            Pageable pageable);
}
