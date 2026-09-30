package com.asiscontrol.controller;

import com.asiscontrol.dto.asistencia.AsistenciaDtos.ActualizarAsistenciaAlumnoRequest;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.AsistenciaAlumnoResponse;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.RegistrarAsistenciaAlumnoLoteRequest;
import com.asiscontrol.dto.asistencia.AsistenciaDtos.RegistrarAsistenciaAlumnoRequest;
import com.asiscontrol.service.AsistenciaAlumnoService;
import com.asiscontrol.service.ModuloAccessGuard;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias/alumnos")
@PreAuthorize("hasAuthority('ASISTENCIA_LEER')")
public class AsistenciaAlumnoController {

    private final AsistenciaAlumnoService asistenciaService;
    private final ModuloAccessGuard accessGuard;

    public AsistenciaAlumnoController(
            AsistenciaAlumnoService asistenciaService,
            ModuloAccessGuard accessGuard
    ) {
        this.asistenciaService = asistenciaService;
        this.accessGuard = accessGuard;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public ResponseEntity<AsistenciaAlumnoResponse> registrar(
            @Valid @RequestBody RegistrarAsistenciaAlumnoRequest request) {
        return ResponseEntity.status(201).body(asistenciaService.registrar(request));
    }

    @PostMapping("/lote")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public ResponseEntity<List<AsistenciaAlumnoResponse>> registrarLote(
            @Valid @RequestBody RegistrarAsistenciaAlumnoLoteRequest request) {
        return ResponseEntity.status(201).body(asistenciaService.registrarLote(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASISTENCIA_ESCRIBIR')")
    public AsistenciaAlumnoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAsistenciaAlumnoRequest request) {
        return asistenciaService.actualizar(id, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@moduloAccessGuard.puedeAccederAsistenciaAlumno(#id)")
    public AsistenciaAlumnoResponse obtener(@PathVariable Long id) {
        return asistenciaService.obtener(id);
    }

    @GetMapping
    public Page<AsistenciaAlumnoResponse> listar(
            @RequestParam(required = false) Long alumnoId,
            @RequestParam(required = false) Long asignacionCursoId,
            @RequestParam(required = false) LocalDate fechaRegistro,
            @PageableDefault(size = 20, sort = "fechaRegistro") Pageable pageable) {
        return asistenciaService.listar(
                alumnoId,
                asignacionCursoId,
                fechaRegistro,
                accessGuard.restriccionDocentePersonaId(),
                accessGuard.restriccionAlumnoPersonaId(),
                accessGuard.restriccionApoderadoPersonaId(),
                pageable);
    }
}
