package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoError;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "SeguimientoError")
public class SeguimientoError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento_error")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_registro_error", nullable = false)
    private RegistroError error;

    @Column(nullable = false, length = 150)
    private String actor;

    @Column(nullable = false)
    private Instant fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoError anterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoError nuevo;

    @Column(nullable = false, length = 1000)
    private String observacion;
}
