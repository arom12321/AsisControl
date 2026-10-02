package com.asiscontrol.controller;

import com.asiscontrol.dto.academico.AcademicoDtos.ActualizarAsignacionCursoRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.AsignacionCursoResponse;
import com.asiscontrol.dto.academico.AcademicoDtos.CrearAsignacionCursoRequest;
import com.asiscontrol.service.AsignacionCursoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asignaciones-cursos")
@PreAuthorize("hasAuthority('ACADEMICO_LEER')")
public class AsignacionCursoController {

    private final AsignacionCursoService asignacionCursoService;

    public AsignacionCursoController(AsignacionCursoService asignacionCursoService) {
        this.asignacionCursoService = asignacionCursoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public AsignacionCursoResponse crear(@Valid @RequestBody CrearAsignacionCursoRequest request) {
        return asignacionCursoService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public AsignacionCursoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAsignacionCursoRequest request) {
        return asignacionCursoService.actualizar(id, request);
    }

    @GetMapping("/{id}")
    public AsignacionCursoResponse obtener(@PathVariable Long id) {
        return asignacionCursoService.obtener(id);
    }

    @GetMapping
    public Page<AsignacionCursoResponse> listar(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        return asignacionCursoService.listar(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public void eliminar(@PathVariable Long id) {
        asignacionCursoService.eliminar(id);
    }
}
