package com.asiscontrol.entity;

import com.asiscontrol.entity.enums.ResultadoAuditoria;
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
@Table(name = "RegistroAuditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_auditoria")
    private Long id;

    @Column(name = "actor_identificador", nullable = false, length = 100)
    private String actorIdentificador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol_actor")
    private Rol rolActor;

    @Column(name = "accion", nullable = false, length = 80)
    private String accion;

    @Column(name = "modulo", nullable = false, length = 80)
    private String modulo;

    @Column(name = "entidad", nullable = false, length = 80)
    private String entidad;

    @Column(name = "registro_id", length = 80)
    private String registroId;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoAuditoria resultado;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "nuevo_valor", columnDefinition = "TEXT")
    private String nuevoValor;

    @Column(name = "correlacion_id", nullable = false, length = 64)
    private String correlacionId;

    @Column(name = "direccion_ip", length = 64)
    private String direccionIp;
}
