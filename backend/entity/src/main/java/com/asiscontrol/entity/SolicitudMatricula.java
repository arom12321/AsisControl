package com.asiscontrol.entity;


import com.asiscontrol.entity.Seccion;
import com.asiscontrol.entity.Alumno;
import com.asiscontrol.entity.AuditableEntity;
import com.asiscontrol.entity.enums.EstadoSolicitudMatricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SolicitudMatricula")
public class SolicitudMatricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_matricula")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_seccion", nullable = false)
    private Seccion seccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_solicitud", nullable = false, length = 30)
    private EstadoSolicitudMatricula estado;

    @Column(name = "fecha_envio")
    private Instant fechaEnvio;

    @Column(name = "fecha_revision")
    private Instant fechaRevision;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "motivo_rechazo", length = 700)
    private String motivoRechazo;
}
