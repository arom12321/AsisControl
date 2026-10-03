package com.asiscontrol.entity;


import com.asiscontrol.entity.AuditableEntity;
import com.asiscontrol.entity.enums.IndicadorRiesgo;
import com.asiscontrol.entity.enums.NivelRiesgo;
import com.asiscontrol.entity.enums.OperadorComparacion;
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

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CriterioRiesgo")
public class CriterioRiesgo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_criterio_riesgo")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 160)
    private String nombre;

    @Column(name = "descripcion", length = 600)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "indicador", nullable = false, length = 50)
    private IndicadorRiesgo indicador;

    @Enumerated(EnumType.STRING)
    @Column(name = "operador", nullable = false, length = 20)
    private OperadorComparacion operador;

    @Column(name = "umbral", nullable = false, precision = 12, scale = 4)
    private BigDecimal umbral;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_riesgo", nullable = false, length = 15)
    private NivelRiesgo nivelRiesgo;
}
