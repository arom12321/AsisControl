package com.asiscontrol.controller;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.auditoria.AuditoriaDtos;
import com.asiscontrol.entity.enums.EstadoError;
import com.asiscontrol.service.AuditoriaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','DIRECTOR')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/registros")
    public PageResponse<AuditoriaDtos.AuditResponse> audits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return auditoriaService.listarAuditorias(page, size);
    }

    @GetMapping("/errores")
    public PageResponse<AuditoriaDtos.ErrorResponse> errors(
            @RequestParam(required = false) EstadoError estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return auditoriaService.listarErrores(estado, page, size);
    }

    @PutMapping("/errores/{id}")
    public AuditoriaDtos.ErrorResponse updateError(
            @PathVariable Long id,
            @Valid @RequestBody AuditoriaDtos.UpdateErrorRequest request
    ) {
        return auditoriaService.actualizarError(id, request);
    }
}
