package com.asiscontrol.dto;

import com.asiscontrol.entity.enums.EstadoAlerta;
import com.asiscontrol.entity.enums.IndicadorRiesgo;
import com.asiscontrol.entity.enums.NivelRiesgo;
import com.asiscontrol.entity.enums.OperadorComparacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class RiesgoDto {

    private RiesgoDto() {
    }

    public record CriterioRequest(
            @NotBlank @Size(max = 160) String nombre,
            @Size(max = 600) String descripcion,
            @NotNull IndicadorRiesgo indicador,
            @NotNull OperadorComparacion operador,
            @NotNull @DecimalMin("0.00") BigDecimal umbral,
            @NotNull NivelRiesgo nivelRiesgo
    ) {
    }

    public record CriterioResponse(
            Long id,
            String nombre,
            String descripcion,
            IndicadorRiesgo indicador,
            OperadorComparacion operador,
            BigDecimal umbral,
            NivelRiesgo nivelRiesgo,
            Instant fechaCreacion,
            Instant fechaActualizacion
    ) {
    }

    public record IndicadorValor(
            @NotNull IndicadorRiesgo indicador,
            @NotNull @DecimalMin("0.00") BigDecimal valor
    ) {
    }

    public record EvaluacionRequest(
            @NotNull Long idAlumno,
            @NotEmpty List<@Valid IndicadorValor> indicadores
    ) {
    }

    public record EstadoAlertaRequest(
            @NotNull EstadoAlerta estado,
            @Size(max = 1000) String observacion
    ) {
    }

    public record AlertaResponse(
            Long id,
            Long idAlumno,
            Long idCriterio,
            String nombreCriterio,
            IndicadorRiesgo indicador,
            BigDecimal valorDetectado,
            BigDecimal umbral,
            NivelRiesgo nivelRiesgo,
            EstadoAlerta estado,
            String mensaje,
            String observacion,
            Instant fechaDeteccion,
            Instant fechaAtencion,
            Instant fechaActualizacion
    ) {
    }
}
