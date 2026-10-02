package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoError;
import com.asiscontrol.entity.enums.SeveridadError;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RegistroError")
public class RegistroError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_error")
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidad", nullable = false, length = 20)
    private SeveridadError severidad;

    @Column(name = "componente", nullable = false, length = 150)
    private String componente;

    @Column(name = "mensaje_seguro", nullable = false, length = 500)
    private String mensajeSeguro;

    @Column(name = "detalle_tecnico_seguro", columnDefinition = "TEXT")
    private String detalleTecnicoSeguro;

    @Column(name = "correlacion_id", nullable = false, length = 64)
    private String correlacionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_error", nullable = false, length = 20)
    private EstadoError estado = EstadoError.PENDIENTE;

    @jakarta.persistence.Version
    @Column(nullable = false)
    private long version;

    @Column(name = "observacion_seguimiento", length = 1000)
    private String observacionSeguimiento;
}
