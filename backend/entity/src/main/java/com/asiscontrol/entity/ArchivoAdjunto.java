package com.asiscontrol.entity;


import com.asiscontrol.entity.Persona;
import com.asiscontrol.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ArchivoAdjunto", uniqueConstraints = {
        @UniqueConstraint(name = "uk_archivo_nombre_almacenado", columnNames = "nombre_almacenado"),
        @UniqueConstraint(name = "uk_archivo_ruta", columnNames = "ruta_archivo")
})
public class ArchivoAdjunto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Long id;

    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @Column(name = "nombre_almacenado", nullable = false, length = 80)
    private String nombreAlmacenado;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "tipo_mime", nullable = false, length = 120)
    private String tipoMime;

    @Column(name = "tamanio_bytes", nullable = false)
    private long tamanioBytes;

    @Column(name = "sha_256", nullable = false, length = 64)
    private String sha256;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona_carga", nullable = false)
    private Persona cargadoPor;
}
