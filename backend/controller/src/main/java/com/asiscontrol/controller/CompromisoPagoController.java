package com.asiscontrol.controller;

import com.asiscontrol.dto.PagoDto;
import com.asiscontrol.entity.enums.EstadoCompromisoPago;
import com.asiscontrol.service.PagoService;
import com.asiscontrol.service.ModuloAccessGuard;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compromisos-pago")
@PreAuthorize("hasAuthority('PAGOS_LEER')")
public class CompromisoPagoController {

    private final PagoService pagoService;
    private final ModuloAccessGuard accessGuard;

    public CompromisoPagoController(PagoService pagoService, ModuloAccessGuard accessGuard) {
        this.pagoService = pagoService;
        this.accessGuard = accessGuard;
    }

    @GetMapping
    public Page<PagoDto.CompromisoResponse> listar(
            @RequestParam(required = false) EstadoCompromisoPago estado,
            @RequestParam(required = false) Long idMatricula,
            @RequestParam(required = false) String busqueda,
            @PageableDefault(size = 20, sort = "fechaVencimiento", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return pagoService.listarCompromisos(
                estado,
                idMatricula,
                busqueda,
                accessGuard.restriccionApoderadoPersonaId(),
                pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@moduloAccessGuard.puedeAccederCompromisoPago(#id)")
    public PagoDto.CompromisoResponse obtener(@PathVariable Long id) {
        return pagoService.obtenerCompromiso(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PAGOS_ESCRIBIR')")
    public PagoDto.CompromisoResponse crear(
            @Valid @RequestBody PagoDto.CompromisoRequest request
    ) {
        return pagoService.crearCompromiso(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAGOS_ESCRIBIR')")
    public PagoDto.CompromisoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoDto.CompromisoRequest request
    ) {
        return pagoService.actualizarCompromiso(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('PAGOS_ESCRIBIR')")
    public void eliminar(@PathVariable Long id) {
        pagoService.eliminarCompromiso(id);
    }
}
