package com.asiscontrol.entity;


import com.asiscontrol.entity.Alumno;
import com.asiscontrol.entity.AuditableEntity;
import com.asiscontrol.entity.enums.EstadoAlerta;
import com.asiscontrol.entity.enums.NivelRiesgo;
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

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "AlertaRiesgo")
public class AlertaRiesgo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta_riesgo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_criterio_riesgo", nullable = false)
    private CriterioRiesgo criterio;

    @Column(name = "valor_detectado", nullable = false, precision = 12, scale = 4)
    private BigDecimal valorDetectado;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo", nullable = false, length = 15)
    private NivelRiesgo nivelRiesgo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_alerta", nullable = false, length = 20)
    private EstadoAlerta estado = EstadoAlerta.ACTIVA;

    @Column(name = "mensaje", nullable = false, length = 600)
    private String mensaje;

    @Column(name = "fecha_deteccion", nullable = false)
    private Instant fechaDeteccion;

    @Column(name = "fecha_atencion")
    private Instant fechaAtencion;

    @Column(name = "observacion", length = 1000)
    private String observacion;
}
