package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoMatricula;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Matricula", uniqueConstraints = {
        @UniqueConstraint(name = "uk_matricula_codigo", columnNames = "codigo_matricula"),
        @UniqueConstraint(name = "uk_matricula_solicitud", columnNames = "id_solicitud_matricula"),
        @UniqueConstraint(
                name = "uk_matricula_alumno_anio",
                columnNames = {"id_alumno", "anio_academico"}
        )
})
public class Matricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long id;

    @Column(name = "codigo_matricula", nullable = false, length = 50)
    private String codigoMatricula;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud_matricula", nullable = false)
    private SolicitudMatricula solicitudMatricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_seccion", nullable = false)
    private Seccion seccion;

    @Column(name = "anio_academico", nullable = false)
    private int anioAcademico;

    @Column(name = "fecha_matricula", nullable = false)
    private Instant fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_matricula", nullable = false, length = 20)
    private EstadoMatricula estado;
}
