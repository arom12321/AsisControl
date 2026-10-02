package com.asiscontrol.repository;

import com.asiscontrol.entity.TokenRecuperacion;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    @Modifying
    @Query("delete from TokenRecuperacion t where t.expira<:antes")
    int limpiar(@Param("antes") Instant antes);

    Optional<TokenRecuperacion> findByTokenHash(String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TokenRecuperacion t where t.tokenHash=:hash")
    Optional<TokenRecuperacion> bloquear(@Param("hash") String hash);

    @Modifying
    @Query(
            "update TokenRecuperacion t set t.revocado=:ahora where t.usuario.id=:id and t.usado is"
                + " null and t.revocado is null")
    int revocar(@Param("id") Long id, @Param("ahora") Instant ahora);
}
