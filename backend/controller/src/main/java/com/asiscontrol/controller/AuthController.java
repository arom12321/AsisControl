package com.asiscontrol.controller;

import com.asiscontrol.dto.auth.AuthDtos;
import com.asiscontrol.exception.BusinessRuleException;
import com.asiscontrol.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final com.asiscontrol.service.RecuperacionService recuperacion;

    public AuthController(
            AuthService authService, com.asiscontrol.service.RecuperacionService recuperacion) {
        this.authService = authService;
        this.recuperacion = recuperacion;
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        authService.logout(extractBearerToken(authorization));
    }

    @GetMapping("/me")
    public AuthDtos.UserSummary me(@AuthenticationPrincipal Jwt jwt) {
        return authService.getCurrentUser(jwt);
    }

    @PostMapping("/change-password")
    public AuthDtos.MessageResponse changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AuthDtos.ChangePasswordRequest request) {
        authService.changePassword(jwt, request);
        return new AuthDtos.MessageResponse(
                "La contrasena fue actualizada; inicie sesion nuevamente");
    }

    @PostMapping("/recovery/request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthDtos.MessageResponse solicitar(
            @Valid @RequestBody AuthDtos.RecoveryRequest request,
            jakarta.servlet.http.HttpServletRequest http) {
        // Solo IP del transporte: no confiar en X-Forwarded-For suministrado por el cliente.
        recuperacion.solicitar(request.identifier(), http.getRemoteAddr());
        return new AuthDtos.MessageResponse(com.asiscontrol.service.RecuperacionService.RESPUESTA);
    }

    @PostMapping("/recovery/validate")
    public AuthDtos.MessageResponse validar(
            @Valid @RequestBody AuthDtos.RecoveryTokenRequest request) {
        recuperacion.validar(request.token());
        return new AuthDtos.MessageResponse("Enlace válido");
    }

    @PostMapping("/recovery/reset")
    public AuthDtos.MessageResponse recuperar(
            @Valid @RequestBody AuthDtos.ResetRecoveryRequest request) {
        recuperacion.confirmar(request);
        return new AuthDtos.MessageResponse("Contraseña actualizada. Inicie sesión nuevamente");
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessRuleException("TOKEN_FALTANTE", "Debe enviar un token Bearer valido");
        }
        return authorization.substring(7);
    }
}
