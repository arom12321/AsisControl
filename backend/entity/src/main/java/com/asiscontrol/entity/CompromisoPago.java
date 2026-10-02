package com.asiscontrol.entity;


import com.asiscontrol.entity.Matricula;
import com.asiscontrol.entity.AuditableEntity;
import com.asiscontrol.entity.enums.EstadoCompromisoPago;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CompromisoPago", uniqueConstraints = {
        @UniqueConstraint(name = "uk_compromiso_pago_codigo", columnNames = "codigo_compromiso")
})
public class CompromisoPago extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compromiso_pago")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_matricula", nullable = false)
    private Matricula matricula;

    @Column(name = "codigo_compromiso", nullable = false, length = 50)
    private String codigoCompromiso;

    @Column(name = "concepto", nullable = false, length = 160)
    private String concepto;

    @Column(name = "descripcion", length = 600)
    private String descripcion;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "PEN";

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_compromiso", nullable = false, length = 20)
    private EstadoCompromisoPago estado = EstadoCompromisoPago.PENDIENTE;
}
