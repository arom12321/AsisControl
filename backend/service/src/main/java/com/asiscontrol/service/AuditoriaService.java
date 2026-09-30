package com.asiscontrol.service;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.auditoria.AuditoriaDtos;
import com.asiscontrol.entity.AccesoUsuario;
import com.asiscontrol.entity.RegistroAuditoria;
import com.asiscontrol.entity.RegistroError;
import com.asiscontrol.entity.enums.EstadoError;
import com.asiscontrol.entity.enums.ResultadoAuditoria;
import com.asiscontrol.entity.enums.SeveridadError;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.AccesoUsuarioRepository;
import com.asiscontrol.repository.RegistroAuditoriaRepository;
import com.asiscontrol.repository.RegistroErrorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditoriaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaService.class);

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final RegistroErrorRepository errorRepository;
    private final AccesoUsuarioRepository usuarioRepository;

    public AuditoriaService(
            RegistroAuditoriaRepository auditoriaRepository,
            RegistroErrorRepository errorRepository,
            AccesoUsuarioRepository usuarioRepository
    ) {
        this.auditoriaRepository = auditoriaRepository;
        this.errorRepository = errorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
            String accion,
            String modulo,
            String entidad,
            String registroId,
            ResultadoAuditoria resultado,
            String motivo,
            String valorAnterior,
            String nuevoValor
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String identifier = authentication != null && authentication.isAuthenticated()
                    ? authentication.getName()
                    : "SISTEMA";
            RegistroAuditoria audit = new RegistroAuditoria();
            audit.setActorIdentificador(identifier);
            usuarioRepository.findActiveByIdentifier(identifier)
                    .map(AccesoUsuario::getRol)
                    .ifPresent(audit::setRolActor);
            audit.setAccion(accion);
            audit.setModulo(modulo);
            audit.setEntidad(entidad);
            audit.setRegistroId(registroId);
            audit.setFechaHora(Instant.now());
            audit.setResultado(resultado);
            audit.setMotivo(motivo);
            audit.setValorAnterior(valorAnterior);
            audit.setNuevoValor(nuevoValor);
            audit.setCorrelacionId(correlationId());
            auditoriaRepository.save(audit);
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible persistir el registro de auditoria", exception);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarError(
            SeveridadError severidad,
            String componente,
            String mensajeSeguro,
            String detalleTecnicoSeguro
    ) {
        RegistroError error = new RegistroError();
        error.setFechaHora(Instant.now());
        error.setSeveridad(severidad);
        error.setComponente(componente);
        error.setMensajeSeguro(mensajeSeguro);
        error.setDetalleTecnicoSeguro(detalleTecnicoSeguro);
        error.setCorrelacionId(correlationId());
        errorRepository.save(error);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditoriaDtos.AuditResponse> listarAuditorias(int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaHora")
        );
        return PageResponse.from(auditoriaRepository.findAll(pageable), this::toAuditResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditoriaDtos.ErrorResponse> listarErrores(
            EstadoError estado,
            int page,
            int size
    ) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaHora")
        );
        return estado == null
                ? PageResponse.from(errorRepository.findAll(pageable), this::toErrorResponse)
                : PageResponse.from(
                        errorRepository.findAllByEstadoOrderByFechaHoraDesc(estado, pageable),
                        this::toErrorResponse
                );
    }

    @Transactional
    public AuditoriaDtos.ErrorResponse actualizarError(
            Long id,
            AuditoriaDtos.UpdateErrorRequest request
    ) {
        RegistroError error = errorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ERROR_NO_ENCONTRADO",
                        "No se encontro el registro de error"
                ));
        error.setEstado(request.estado());
        error.setObservacionSeguimiento(request.observacionSeguimiento());
        return toErrorResponse(errorRepository.save(error));
    }

    private AuditoriaDtos.AuditResponse toAuditResponse(RegistroAuditoria audit) {
        return new AuditoriaDtos.AuditResponse(
                audit.getId(),
                audit.getActorIdentificador(),
                audit.getRolActor() == null ? null : audit.getRolActor().getNombre(),
                audit.getAccion(),
                audit.getModulo(),
                audit.getEntidad(),
                audit.getRegistroId(),
                audit.getFechaHora(),
                audit.getResultado(),
                audit.getMotivo(),
                audit.getCorrelacionId()
        );
    }

    private AuditoriaDtos.ErrorResponse toErrorResponse(RegistroError error) {
        return new AuditoriaDtos.ErrorResponse(
                error.getId(),
                error.getFechaHora(),
                error.getSeveridad(),
                error.getComponente(),
                error.getMensajeSeguro(),
                error.getCorrelacionId(),
                error.getEstado(),
                error.getObservacionSeguimiento()
        );
    }

    private String correlationId() {
        String value = MDC.get("correlationId");
        return value == null ? "sin-correlacion" : value;
    }
}
