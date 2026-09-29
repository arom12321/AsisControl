package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.EstadoAcceso;
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
@Table(name = "AccesoUsuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_acceso_usuario_persona", columnNames = "id_persona"),
        @UniqueConstraint(name = "uk_acceso_usuario_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_acceso_usuario_correo", columnNames = "correo")
})
public class AccesoUsuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso_usuario")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "username", nullable = false, length = 80)
    private String username;

    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 100)
    private String contrasenaHash;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "requiere_cambio_contrasena", nullable = false)
    private boolean requiereCambioContrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_acceso", nullable = false, length = 35)
    private EstadoAcceso estado = EstadoAcceso.PENDIENTE;

    @Column(name = "bloqueado_hasta")
    private Instant bloqueadoHasta;

    @Column(name = "ultimo_acceso")
    private Instant ultimoAcceso;
}
