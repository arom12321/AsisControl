package com.asiscontrol.controller;

import com.asiscontrol.dto.academico.AcademicoDtos.ActualizarSeccionRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.CrearSeccionRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.SeccionResponse;
import com.asiscontrol.service.SeccionService;
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
@RequestMapping("/api/secciones")
@PreAuthorize("hasAuthority('ACADEMICO_LEER')")
public class SeccionController {

    private final SeccionService seccionService;

    public SeccionController(SeccionService seccionService) {
        this.seccionService = seccionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public SeccionResponse crear(@Valid @RequestBody CrearSeccionRequest request) {
        return seccionService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public SeccionResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarSeccionRequest request) {
        return seccionService.actualizar(id, request);
    }

    @GetMapping("/{id}")
    public SeccionResponse obtener(@PathVariable Long id) {
        return seccionService.obtener(id);
    }

    @GetMapping
    public Page<SeccionResponse> listar(
            @PageableDefault(size = 20, sort = {"anioAcademico", "grado", "nombre"}) Pageable pageable) {
        return seccionService.listar(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ACADEMICO_ESCRIBIR')")
    public void eliminar(@PathVariable Long id) {
        seccionService.eliminar(id);
    }
}
