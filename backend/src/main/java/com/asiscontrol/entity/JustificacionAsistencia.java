package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoJustificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "JustificacionAsistencia", uniqueConstraints = {
        @UniqueConstraint(name = "uk_justificacion_asistencia", columnNames = "id_asistencia_alumno")
})
public class JustificacionAsistencia extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_justificacion_asistencia")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asistencia_alumno", nullable = false)
    private AsistenciaAlumno asistenciaAlumno;

    @Column(name = "motivo", nullable = false, length = 2000)
    private String motivo;

    @Column(name = "fecha_hora_envio", nullable = false)
    private LocalDateTime fechaHoraEnvio;

    @Column(name = "fecha_hora_revision")
    private LocalDateTime fechaHoraRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente_revisor")
    private Docente docenteRevisor;

    @Column(name = "observacion_revision", length = 2000)
    private String observacionRevision;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_justificacion", nullable = false, length = 20)
    private EstadoJustificacion estado = EstadoJustificacion.ENVIADA;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "JustificacionAsistenciaArchivo",
            joinColumns = @JoinColumn(name = "id_justificacion_asistencia"),
            inverseJoinColumns = @JoinColumn(name = "id_archivo"))
    private Set<ArchivoAdjunto> archivos = new LinkedHashSet<>();
}
