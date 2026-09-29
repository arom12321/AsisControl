package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoAsistencia;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "AsistenciaDocente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_asistencia_docente_jornada", columnNames = {"id_docente", "fecha_jornada"}),
        @UniqueConstraint(name = "uk_asistencia_docente_qr", columnNames = "id_codigo_qr")
})
public class AsistenciaDocente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia_docente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_codigo_qr")
    private CodigoQRAsistencia codigoQR;

    @Column(name = "fecha_jornada", nullable = false)
    private LocalDate fechaJornada;

    @Column(name = "fecha_hora_marcada", nullable = false)
    private LocalDateTime fechaHoraMarcada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", nullable = false, length = 35)
    private EstadoAsistencia estado;

    @Column(name = "es_contingencia", nullable = false)
    private boolean contingencia;

    @Column(name = "motivo_contingencia", length = 1000)
    private String motivoContingencia;
}
