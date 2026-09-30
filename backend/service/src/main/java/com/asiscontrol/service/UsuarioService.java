package com.asiscontrol.service;


import com.asiscontrol.service.AuditoriaService;
import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.seguridad.UsuarioDtos;
import com.asiscontrol.entity.AccesoUsuario;
import com.asiscontrol.entity.Permiso;
import com.asiscontrol.entity.Persona;
import com.asiscontrol.entity.Rol;
import com.asiscontrol.entity.enums.EstadoAcceso;
import com.asiscontrol.entity.enums.EstadoSesion;
import com.asiscontrol.entity.enums.ResultadoAuditoria;
import com.asiscontrol.exception.ConflictException;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.AccesoUsuarioRepository;
import com.asiscontrol.repository.PermisoRepository;
import com.asiscontrol.repository.PersonaRepository;
import com.asiscontrol.repository.RolRepository;
import com.asiscontrol.repository.SesionAccesoRepository;
import com.asiscontrol.util.PageUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "username", "correo", "estado", "fechaCreacion"
    );

    private final AccesoUsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final SesionAccesoRepository sesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
            AccesoUsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            RolRepository rolRepository,
            PermisoRepository permisoRepository,
            SesionAccesoRepository sesionRepository,
            PasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.sesionRepository = sesionRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioDtos.Response> list(
            String search,
            EstadoAcceso estado,
            Long roleId,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Pageable pageable = PageUtils.create(
                page,
                size,
                sortBy,
                direction,
                SORT_FIELDS,
                "fechaCreacion"
        );
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        return PageResponse.from(
                usuarioRepository.searchActive(normalizedSearch, estado, roleId, pageable),
                this::toResponse
        );
    }

    @Transactional(readOnly = true)
    public UsuarioDtos.Response get(Long id) {
        return toResponse(findActive(id));
    }

    @Transactional
    public UsuarioDtos.Response create(UsuarioDtos.CreateRequest request) {
        Persona persona = personaRepository.findByIdAndActivoTrue(request.personaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PERSONA_NO_ENCONTRADA",
                        "No se encontro la persona"
                ));
        if (usuarioRepository.findByPersonaIdAndActivoTrue(persona.getId()).isPresent()) {
            throw new ConflictException(
                    "PERSONA_CON_USUARIO",
                    "La persona ya tiene una cuenta de acceso"
            );
        }
        ensureUniqueCredentials(request.username(), request.correo(), null);
        Rol role = findActiveRole(request.rolId());

        AccesoUsuario usuario = new AccesoUsuario();
        usuario.setPersona(persona);
        usuario.setRol(role);
        usuario.setUsername(request.username().trim());
        usuario.setCorreo(request.correo().trim().toLowerCase());
        usuario.setContrasenaHash(passwordEncoder.encode(request.password()));
        usuario.setRequiereCambioContrasena(request.requiereCambioContrasena());
        usuario.setEstado(EstadoAcceso.ACTIVO);
        AccesoUsuario saved = usuarioRepository.save(usuario);
        auditoriaService.registrar(
                "CREAR_USUARIO",
                "SEGURIDAD",
                "AccesoUsuario",
                saved.getId().toString(),
                ResultadoAuditoria.EXITOSO,
                null,
                null,
                "rol=" + role.getNombre()
        );
        return toResponse(saved);
    }

    @Transactional
    public UsuarioDtos.Response update(Long id, UsuarioDtos.UpdateRequest request) {
        AccesoUsuario usuario = findActive(id);
        ensureUniqueCredentials(usuario.getUsername(), request.correo(), usuario.getId());
        Rol role = findActiveRole(request.rolId());
        String previous = "rol=" + usuario.getRol().getNombre() + ",estado=" + usuario.getEstado();
        usuario.setRol(role);
        usuario.setCorreo(request.correo().trim().toLowerCase());
        usuario.setEstado(request.estado());
        if (request.estado() != EstadoAcceso.BLOQUEADO_TEMPORALMENTE) {
            usuario.setBloqueadoHasta(null);
            usuario.setIntentosFallidos(0);
        }
        AccesoUsuario saved = usuarioRepository.save(usuario);
        if (request.estado() != EstadoAcceso.ACTIVO) {
            sesionRepository.revokeAllByUsuarioId(saved.getId(), EstadoSesion.REVOCADA, Instant.now());
        }
        auditoriaService.registrar(
                "ACTUALIZAR_USUARIO",
                "SEGURIDAD",
                "AccesoUsuario",
                id.toString(),
                ResultadoAuditoria.EXITOSO,
                null,
                previous,
                "rol=" + role.getNombre() + ",estado=" + request.estado()
        );
        return toResponse(saved);
    }

    @Transactional
    public void resetPassword(Long id, UsuarioDtos.ResetPasswordRequest request) {
        AccesoUsuario usuario = findActive(id);
        usuario.setContrasenaHash(passwordEncoder.encode(request.temporaryPassword()));
        usuario.setRequiereCambioContrasena(true);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        if (usuario.getEstado() == EstadoAcceso.BLOQUEADO_TEMPORALMENTE) {
            usuario.setEstado(EstadoAcceso.ACTIVO);
        }
        usuarioRepository.save(usuario);
        sesionRepository.revokeAllByUsuarioId(id, EstadoSesion.REVOCADA, Instant.now());
        auditoriaService.registrar(
                "RESTABLECER_CONTRASENA",
                "SEGURIDAD",
                "AccesoUsuario",
                id.toString(),
                ResultadoAuditoria.EXITOSO,
                null,
                null,
                null
        );
    }

    @Transactional
    public void delete(Long id) {
        AccesoUsuario usuario = findActive(id);
        usuario.setActivo(false);
        usuario.setEstado(EstadoAcceso.DESACTIVADO);
        usuarioRepository.save(usuario);
        sesionRepository.revokeAllByUsuarioId(id, EstadoSesion.REVOCADA, Instant.now());
        auditoriaService.registrar(
                "DESACTIVAR_USUARIO",
                "SEGURIDAD",
                "AccesoUsuario",
                id.toString(),
                ResultadoAuditoria.EXITOSO,
                null,
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioDtos.RolResponse> listRoles() {
        return rolRepository.findAllByActivoTrueOrderByNombreAsc().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional
    public UsuarioDtos.RolResponse createRole(UsuarioDtos.RolRequest request) {
        if (rolRepository.findByNombreIgnoreCase(request.nombre().trim()).isPresent()) {
            throw new ConflictException("ROL_DUPLICADO", "Ya existe un rol con ese nombre");
        }
        Rol role = new Rol();
        applyRoleRequest(role, request);
        return toRoleResponse(rolRepository.save(role));
    }

    @Transactional
    public UsuarioDtos.RolResponse updateRole(Long id, UsuarioDtos.RolRequest request) {
        Rol role = findActiveRole(id);
        rolRepository.findByNombreIgnoreCase(request.nombre().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("ROL_DUPLICADO", "Ya existe un rol con ese nombre");
                });
        applyRoleRequest(role, request);
        return toRoleResponse(rolRepository.save(role));
    }

    @Transactional
    public void deleteRole(Long id) {
        Rol role = findActiveRole(id);
        if (usuarioRepository.existsByRolIdAndActivoTrue(id)) {
            throw new ConflictException(
                    "ROL_EN_USO",
                    "El rol no puede desactivarse porque tiene usuarios asociados"
            );
        }
        role.setActivo(false);
        rolRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDtos.PermisoResponse> listPermissions() {
        return permisoRepository.findAllByActivoTrueOrderByCodigoAsc().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    private void applyRoleRequest(Rol role, UsuarioDtos.RolRequest request) {
        role.setNombre(request.nombre().trim().toUpperCase());
        role.setDescripcion(request.descripcion());
        Set<Long> ids = request.permisoIds() == null ? Set.of() : request.permisoIds();
        List<Permiso> permissions = permisoRepository.findAllById(ids);
        if (permissions.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "PERMISO_NO_ENCONTRADO",
                    "Uno o mas permisos no existen"
            );
        }
        role.setPermisos(new HashSet<>(permissions));
    }

    private void ensureUniqueCredentials(String username, String email, Long currentId) {
        usuarioRepository.findActiveByIdentifier(username)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ConflictException("USERNAME_DUPLICADO", "El nombre de usuario ya esta en uso");
                });
        usuarioRepository.findActiveByIdentifier(email)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ConflictException("CORREO_DUPLICADO", "El correo ya esta en uso");
                });
    }

    private AccesoUsuario findActive(Long id) {
        return usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USUARIO_NO_ENCONTRADO",
                        "No se encontro el usuario"
                ));
    }

    private Rol findActiveRole(Long id) {
        return rolRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ROL_NO_ENCONTRADO",
                        "No se encontro el rol"
                ));
    }

    private UsuarioDtos.Response toResponse(AccesoUsuario usuario) {
        return new UsuarioDtos.Response(
                usuario.getId(),
                usuario.getPersona().getId(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getPersona().nombreCompleto(),
                usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                usuario.getEstado(),
                usuario.isRequiereCambioContrasena(),
                usuario.getIntentosFallidos(),
                usuario.getBloqueadoHasta(),
                usuario.getUltimoAcceso(),
                usuario.getFechaCreacion()
        );
    }

    private UsuarioDtos.RolResponse toRoleResponse(Rol role) {
        return new UsuarioDtos.RolResponse(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getPermisos().stream()
                        .filter(Permiso::isActivo)
                        .map(this::toPermissionResponse)
                        .sorted(java.util.Comparator.comparing(UsuarioDtos.PermisoResponse::codigo))
                        .toList(),
                role.isActivo()
        );
    }

    private UsuarioDtos.PermisoResponse toPermissionResponse(Permiso permission) {
        return new UsuarioDtos.PermisoResponse(
                permission.getId(),
                permission.getCodigo(),
                permission.getNombre(),
                permission.getDescripcion(),
                permission.getCategoria() == null ? null : permission.getCategoria().getNombre()
        );
    }
}
