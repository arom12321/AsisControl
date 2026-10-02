package com.asiscontrol.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "TokenRecuperacion",
        uniqueConstraints =
                @UniqueConstraint(name = "uk_recuperacion_hash", columnNames = "token_hash"))
public class TokenRecuperacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_acceso_usuario", nullable = false)
    private AccesoUsuario usuario;

    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private Instant creado;

    @Column(nullable = false)
    private Instant expira;

    private Instant usado;
    private Instant revocado;
}
