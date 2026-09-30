package com.asiscontrol.controller;

import com.asiscontrol.dto.RiesgoDto;
import com.asiscontrol.service.RiesgoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones-riesgo")
@PreAuthorize("hasAuthority('RIESGOS_ESCRIBIR')")
public class EvaluacionRiesgoController {

    private final RiesgoService riesgoService;

    public EvaluacionRiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @PostMapping
    public List<RiesgoDto.AlertaResponse> evaluar(
            @Valid @RequestBody RiesgoDto.EvaluacionRequest request
    ) {
        return riesgoService.evaluar(request);
    }
}
