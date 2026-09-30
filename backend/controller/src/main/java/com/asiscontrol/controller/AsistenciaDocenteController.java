package com.asiscontrol.controller;

import com.asiscontrol.dto.asistencia.AsistenciaDtos.AsistenciaDocenteResponse;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.CodigoQRResponse;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.EmitirCodigoQRRequest;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.EscanearCodigoQRRequest;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.RegistrarContingenciaRequest;
import com.asiscontrol.service.AsistenciaDocenteService;
import com.asiscontrol.service.ModuloAccessGuard;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/asistencias/docentes")
@PreAuthorize("hasAuthority('ASISTENCIA_LEER') and "
        + "hasAnyRole('ADMINISTRADOR','DIRECTOR','SECRETARIA','DOCENTE')")
public class AsistenciaDocenteController {

    private final AsistenciaDocenteService asistenciaService;
    private final ModuloAccessGuard accessGuard;

    public AsistenciaDocenteController(
            AsistenciaDocenteService asistenciaService,
            ModuloAccessGuard accessGuard
    ) {
        this.asistenciaService = asistenciaService;
        this.accessGuard = accessGuard;
    }

    @PostMapping("/qr")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public ResponseEntity<CodigoQRResponse> emitir(@Valid @RequestBody EmitirCodigoQRRequest request) {
        return ResponseEntity.status(201).body(asistenciaService.emitir(request));
    }

    @PostMapping("/qr/{id}/renovar")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public CodigoQRResponse renovar(
            @PathVariable Long id,
            @RequestParam(required = false) @Min(30) @Max(900) Integer vigenciaSegundos) {
        return asistenciaService.renovar(id, vigenciaSegundos);
    }

    @GetMapping("/qr/{id}")
    public CodigoQRResponse consultarCodigo(@PathVariable Long id) {
        return asistenciaService.consultarCodigo(id);
    }

    @PostMapping("/qr/{id}/cancelar")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        asistenciaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/marcar")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public AsistenciaDocenteResponse marcar(@Valid @RequestBody EscanearCodigoQRRequest request) {
        return asistenciaService.marcarConToken(request.token());
    }

    @PostMapping("/contingencia")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public ResponseEntity<AsistenciaDocenteResponse> registrarContingencia(
            @Valid @RequestBody RegistrarContingenciaRequest request) {
        return ResponseEntity.status(201).body(asistenciaService.registrarContingencia(request));
    }

    @GetMapping
    public Page<AsistenciaDocenteResponse> listar(
            @RequestParam(required = false) Long docenteId,
            @PageableDefault(size = 20, sort = "fechaJornada") Pageable pageable) {
        return asistenciaService.listar(
                docenteId,
                accessGuard.restriccionDocentePersonaId(),
                pageable);
    }
}
