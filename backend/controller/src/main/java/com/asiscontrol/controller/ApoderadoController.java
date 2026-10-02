package com.asiscontrol.controller;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.persona.PersonaDtos;
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
@RequestMapping("/api/apoderados")
public class ApoderadoController {

    private final PersonaService personaService;

    public ApoderadoController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','DIRECTOR','SECRETARIA')")
    public PageResponse<PersonaDtos.ApoderadoResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "persona.apellidoPaterno") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return personaService.listApoderados(search, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@moduloAccessGuard.puedeAccederApoderado(#id)")
    public PersonaDtos.ApoderadoResponse get(@PathVariable Long id) {
        return personaService.getApoderado(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SECRETARIA')")
    public PersonaDtos.ApoderadoResponse create(
            @Valid @RequestBody PersonaDtos.ApoderadoCreateRequest request
    ) {
        return personaService.createApoderado(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SECRETARIA')")
    public PersonaDtos.ApoderadoResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PersonaDtos.ApoderadoUpdateRequest request
    ) {
        return personaService.updateApoderado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void delete(@PathVariable Long id) {
        personaService.deleteApoderado(id);
    }
}
