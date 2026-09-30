package com.asiscontrol.controller;

import com.asiscontrol.dto.PagoDto;
import com.asiscontrol.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulaciones-pago")
@PreAuthorize("hasAuthority('PAGOS_LEER')")
public class SimulacionPagoController {

    private final PagoService pagoService;

    public SimulacionPagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public PagoDto.SimulacionResponse simular(
            @Valid @RequestBody PagoDto.SimulacionRequest request
    ) {
        return pagoService.simular(request);
    }
}
