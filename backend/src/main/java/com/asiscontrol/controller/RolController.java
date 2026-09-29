package com.asiscontrol.controller;

import com.asiscontrol.dto.seguridad.UsuarioDtos;
import com.asiscontrol.service.UsuarioService;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {

    private final UsuarioService usuarioService;

    public RolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDtos.RolResponse> list() {
        return usuarioService.listRoles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDtos.RolResponse create(@Valid @RequestBody UsuarioDtos.RolRequest request) {
        return usuarioService.createRole(request);
    }

    @PutMapping("/{id}")
    public UsuarioDtos.RolResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDtos.RolRequest request
    ) {
        return usuarioService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        usuarioService.deleteRole(id);
    }

    @GetMapping("/permisos")
    public List<UsuarioDtos.PermisoResponse> permissions() {
        return usuarioService.listPermissions();
    }
}
