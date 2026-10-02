package com.asiscontrol.controller;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.academico.ConfiguracionDtos.*;
import com.asiscontrol.service.ConfiguracionAcademicaService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion-academica")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ConfiguracionAcademicaController {
    private final ConfiguracionAcademicaService configuracionService;

    public ConfiguracionAcademicaController(ConfiguracionAcademicaService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping("/anios")
    public PageResponse<AnioResponse> listar(
            @PageableDefault(size = 20, sort = "anio") Pageable page) {
        return configuracionService.listar(page);
    }

    @PostMapping("/anios")
    @ResponseStatus(HttpStatus.CREATED)
    public AnioResponse crear(@Valid @RequestBody CrearAnio request) {
        return configuracionService.crear(request);
    }

    @GetMapping("/anios/{anio}")
    public AnioResponse obtener(@PathVariable int anio) {
        return configuracionService.obtener(anio);
    }

    @PutMapping("/anios/{anio}/estado")
    public AnioResponse estado(@PathVariable int anio, @Valid @RequestBody EstadoAnio request) {
        return configuracionService.estado(anio, request);
    }

    @PutMapping("/anios/{anio}/admision")
    public AnioResponse admision(@PathVariable int anio, @Valid @RequestBody AdmisionAnio request) {
        return configuracionService.admision(anio, request);
    }

    @PostMapping("/anios/{anio}/periodos")
    @ResponseStatus(HttpStatus.CREATED)
    public AnioResponse periodo(@PathVariable int anio, @Valid @RequestBody CrearPeriodo request) {
        return configuracionService.agregarPeriodo(anio, request);
    }

    @PutMapping("/periodos/{id}/estado")
    public AnioResponse estadoPeriodo(
            @PathVariable Long id, @Valid @RequestBody EstadoPeriodo request) {
        return configuracionService.estadoPeriodo(id, request);
    }

    @PostMapping("/anios/{anio}/grados")
    @ResponseStatus(HttpStatus.CREATED)
    public AnioResponse grado(@PathVariable int anio, @Valid @RequestBody CrearGrado request) {
        return configuracionService.agregarGrado(anio, request);
    }

    @PutMapping("/grados/{id}")
    public AnioResponse estadoGrado(
            @PathVariable Long id, @Valid @RequestBody EstadoGrado request) {
        return configuracionService.estadoGrado(id, request);
    }
}
