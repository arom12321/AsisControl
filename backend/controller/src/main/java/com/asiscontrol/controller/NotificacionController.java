package com.asiscontrol.controller;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.notificacion.NotificacionDtos;
import com.asiscontrol.service.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public PageResponse<NotificacionDtos.Response> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return notificacionService.list(jwt, page, size);
    }

    @GetMapping("/resumen")
    public NotificacionDtos.Summary summary(@AuthenticationPrincipal Jwt jwt) {
        return notificacionService.summary(jwt);
    }

    @PutMapping("/{id}/leida")
    public NotificacionDtos.Response markRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        return notificacionService.markRead(jwt, id);
    }

    @PutMapping("/lectura")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@AuthenticationPrincipal Jwt jwt) {
        notificacionService.markAllRead(jwt);
    }
}
