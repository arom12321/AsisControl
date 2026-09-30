package com.asiscontrol.controller;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.persona.PersonaDtos;
import com.asiscontrol.entity.enums.Especialidad;
import com.asiscontrol.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    private final PersonaService personaService;

    public DocenteController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','DIRECTOR','SECRETARIA')")
    public PageResponse<PersonaDtos.DocenteResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Especialidad especialidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "persona.apellidoPaterno") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return personaService.listDocentes(search, especialidad, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@moduloAccessGuard.puedeAccederDocente(#id)")
    public PersonaDtos.DocenteResponse get(@PathVariable Long id) {
        return personaService.getDocente(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SECRETARIA')")
    public PersonaDtos.DocenteResponse create(
            @Valid @RequestBody PersonaDtos.DocenteCreateRequest request
    ) {
        return personaService.createDocente(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SECRETARIA')")
    public PersonaDtos.DocenteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PersonaDtos.DocenteUpdateRequest request
    ) {
        return personaService.updateDocente(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Long id) {
        personaService.deleteDocente(id);
    }
}
