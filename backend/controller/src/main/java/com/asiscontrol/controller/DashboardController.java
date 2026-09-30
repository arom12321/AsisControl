package com.asiscontrol.controller;

import com.asiscontrol.dto.dashboard.DashboardDtos.DashboardResponse;
import com.asiscontrol.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Indicadores del panel adaptados al rol autenticado")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Obtener los indicadores visibles para el usuario autenticado")
    public DashboardResponse obtener(Authentication authentication) {
        return dashboardService.obtener(authentication);
    }
}
