package com.asiscontrol.service;

import com.asiscontrol.dto.matricula.MatriculaDtos.ActualizarSolicitudMatriculaRequest;
import com.asiscontrol.dto.matricula.MatriculaDtos.CrearSolicitudMatriculaRequest;
import com.asiscontrol.dto.matricula.MatriculaDtos.SolicitudMatriculaResponse;
import com.asiscontrol.entity.Alumno;
import com.asiscontrol.entity.Seccion;
import com.asiscontrol.entity.SolicitudMatricula;
import com.asiscontrol.entity.enums.EstadoSolicitudMatricula;
import com.asiscontrol.exception.BusinessRuleException;
import com.asiscontrol.exception.ConflictException;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.AlumnoRepository;
import com.asiscontrol.repository.MatriculaRepository;
import com.asiscontrol.repository.SeccionRepository;
import com.asiscontrol.repository.SolicitudMatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SolicitudMatriculaService {

    private static final Set<EstadoSolicitudMatricula> ESTADOS_ABIERTOS = EnumSet.of(
            EstadoSolicitudMatricula.BORRADOR,
            EstadoSolicitudMatricula.ENVIADA,
            EstadoSolicitudMatricula.EN_REVISION,
            EstadoSolicitudMatricula.OBSERVADA
    );

    private final SolicitudMatriculaRepository solicitudRepository;
    private final AlumnoRepository alumnoRepository;
    private final SeccionRepository seccionRepository;
    private final MatriculaRepository matriculaRepository;

    public SolicitudMatriculaService(
            SolicitudMatriculaRepository solicitudRepository,
            AlumnoRepository alumnoRepository,
            SeccionRepository seccionRepository,
            MatriculaRepository matriculaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.alumnoRepository = alumnoRepository;
        this.seccionRepository = seccionRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public SolicitudMatriculaResponse crear(CrearSolicitudMatriculaRequest request) {
        Alumno alumno = buscarAlumno(request.alumnoId());
        Seccion seccion = buscarSeccion(request.seccionId());
        validarDisponibilidadSolicitud(alumno.getId(), seccion.getAnioAcademico(), 0);
        validarAlumnoSinMatricula(alumno.getId(), seccion.getAnioAcademico());

        SolicitudMatricula solicitud = new SolicitudMatricula();
        solicitud.setAlumno(alumno);
        solicitud.setSeccion(seccion);
        solicitud.setEstado(EstadoSolicitudMatricula.BORRADOR);
        solicitud.setObservaciones(normalizar(request.observaciones()));
        solicitud.setActivo(true);
        return mapear(solicitudRepository.save(solicitud));
    }

    @Transactional
    public SolicitudMatriculaResponse actualizar(
            Long id,
            ActualizarSolicitudMatriculaRequest request) {
        SolicitudMatricula solicitud = obtenerEntidad(id);
        exigirVersion(solicitud.getVersion(), request.version());

        if (request.seccionId() != null && !request.seccionId().equals(solicitud.getSeccion().getId())) {
            exigirEditable(solicitud);
            Seccion nuevaSeccion = buscarSeccion(request.seccionId());
            long permitidas = nuevaSeccion.getAnioAcademico() == solicitud.getSeccion().getAnioAcademico()
                    && ESTADOS_ABIERTOS.contains(solicitud.getEstado()) ? 1 : 0;
            validarDisponibilidadSolicitud(
                    solicitud.getAlumno().getId(), nuevaSeccion.getAnioAcademico(), permitidas);
            validarAlumnoSinMatricula(solicitud.getAlumno().getId(), nuevaSeccion.getAnioAcademico());
            solicitud.setSeccion(nuevaSeccion);
        }

        if (request.estado() == null || request.estado() == solicitud.getEstado()) {
            exigirEditable(solicitud);
            solicitud.setObservaciones(normalizar(request.observaciones()));
            return mapear(solicitudRepository.save(solicitud));
        }

        transicionar(solicitud, request.estado(), request.observaciones(), request.motivoRechazo());
        return mapear(solicitudRepository.save(solicitud));
    }

    public SolicitudMatriculaResponse obtener(Long id) {
        return mapear(obtenerEntidad(id));
    }

    public Page<SolicitudMatriculaResponse> listar(
            Long personaAlumnoId,
            Long personaApoderadoId,
            Pageable pageable
    ) {
        return solicitudRepository.buscarVisibles(
                        personaAlumnoId,
                        personaApoderadoId,
                        pageable)
                .map(this::mapear);
    }

    @Transactional
    public void eliminar(Long id) {
        SolicitudMatricula solicitud = obtenerEntidad(id);
        if (solicitud.getEstado() != EstadoSolicitudMatricula.BORRADOR
                && solicitud.getEstado() != EstadoSolicitudMatricula.CANCELADA
                && solicitud.getEstado() != EstadoSolicitudMatricula.RECHAZADA) {
            throw new BusinessRuleException(
                    "SOLICITUD_NO_ELIMINABLE",
                    "Solo puede eliminarse una solicitud en borrador, cancelada o rechazada");
        }
        solicitud.setActivo(false);
        solicitudRepository.save(solicitud);
    }

    private SolicitudMatricula obtenerEntidad(Long id) {
        return solicitudRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SOLICITUD_MATRICULA_NO_ENCONTRADA",
                        "No existe una solicitud de matricula activa con id " + id));
    }

    private Alumno buscarAlumno(Long id) {
        return alumnoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ALUMNO_NO_ENCONTRADO", "No existe un alumno activo con id " + id));
    }

    private Seccion buscarSeccion(Long id) {
        return seccionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SECCION_NO_ENCONTRADA", "No existe una seccion activa con id " + id));
    }

    private void transicionar(
            SolicitudMatricula solicitud,
            EstadoSolicitudMatricula destino,
            String observaciones,
            String motivoRechazo) {
        EstadoSolicitudMatricula origen = solicitud.getEstado();
        boolean permitida = switch (origen) {
            case BORRADOR -> destino == EstadoSolicitudMatricula.ENVIADA
                    || destino == EstadoSolicitudMatricula.CANCELADA;
            case ENVIADA -> destino == EstadoSolicitudMatricula.EN_REVISION
                    || destino == EstadoSolicitudMatricula.CANCELADA;
            case EN_REVISION -> destino == EstadoSolicitudMatricula.OBSERVADA
                    || destino == EstadoSolicitudMatricula.RECHAZADA;
            case OBSERVADA -> destino == EstadoSolicitudMatricula.ENVIADA
                    || destino == EstadoSolicitudMatricula.CANCELADA;
            case RECHAZADA, MATRICULA_FINALIZADA, CANCELADA -> false;
        };
        if (!permitida) {
            if (destino == EstadoSolicitudMatricula.MATRICULA_FINALIZADA) {
                throw new BusinessRuleException(
                        "SOLICITUD_FINALIZACION_REQUIERE_MATRICULA",
                        "La solicitud solo se finaliza mediante la creacion atomica de la matricula");
            }
            throw new BusinessRuleException(
                    "SOLICITUD_TRANSICION_INVALIDA",
                    "No se permite pasar de " + origen + " a " + destino);
        }

        Instant ahora = Instant.now();
        if (destino == EstadoSolicitudMatricula.ENVIADA) {
            validarAlumnoSinMatricula(
                    solicitud.getAlumno().getId(), solicitud.getSeccion().getAnioAcademico());
            solicitud.setFechaEnvio(ahora);
            solicitud.setMotivoRechazo(null);
        } else if (destino == EstadoSolicitudMatricula.EN_REVISION) {
            solicitud.setFechaRevision(ahora);
        } else if (destino == EstadoSolicitudMatricula.OBSERVADA) {
            String detalle = normalizar(observaciones);
            if (detalle == null) {
                throw new BusinessRuleException(
                        "SOLICITUD_OBSERVACION_REQUERIDA",
                        "Debe indicar las observaciones que el solicitante debe subsanar");
            }
            solicitud.setObservaciones(detalle);
            solicitud.setFechaRevision(ahora);
        } else if (destino == EstadoSolicitudMatricula.RECHAZADA) {
            String motivo = normalizar(motivoRechazo);
            if (motivo == null) {
                throw new BusinessRuleException(
                        "SOLICITUD_MOTIVO_RECHAZO_REQUERIDO",
                        "Debe registrar el motivo del rechazo");
            }
            solicitud.setMotivoRechazo(motivo);
            solicitud.setFechaRevision(ahora);
        }
        solicitud.setEstado(destino);
    }

    private void exigirEditable(SolicitudMatricula solicitud) {
        if (solicitud.getEstado() != EstadoSolicitudMatricula.BORRADOR
                && solicitud.getEstado() != EstadoSolicitudMatricula.OBSERVADA) {
            throw new BusinessRuleException(
                    "SOLICITUD_NO_EDITABLE",
                    "Solo puede editarse una solicitud en borrador u observada");
        }
    }

    private void validarDisponibilidadSolicitud(Long alumnoId, int anio, long permitidas) {
        long cantidad = solicitudRepository.contarSolicitudesEnEstados(alumnoId, anio, ESTADOS_ABIERTOS);
        if (cantidad > permitidas) {
            throw new ConflictException(
                    "SOLICITUD_MATRICULA_DUPLICADA",
                    "El alumno ya tiene una solicitud abierta para ese anio academico");
        }
    }

    private void validarAlumnoSinMatricula(Long alumnoId, int anio) {
        if (matriculaRepository.existsByAlumnoIdAndAnioAcademico(alumnoId, anio)) {
            throw new ConflictException(
                    "ALUMNO_YA_MATRICULADO",
                    "El alumno ya tiene una matricula para ese anio academico");
        }
    }

    private SolicitudMatriculaResponse mapear(SolicitudMatricula solicitud) {
        return new SolicitudMatriculaResponse(
                solicitud.getId(), solicitud.getAlumno().getId(), solicitud.getAlumno().getCodigoAlumno(),
                solicitud.getAlumno().getPersona().nombreCompleto(), solicitud.getSeccion().getId(),
                solicitud.getSeccion().getNombre(), solicitud.getSeccion().getGrado(),
                solicitud.getSeccion().getAnioAcademico(), solicitud.getEstado(), solicitud.getFechaEnvio(),
                solicitud.getFechaRevision(), solicitud.getObservaciones(), solicitud.getMotivoRechazo(),
                solicitud.isActivo(), solicitud.getVersion());
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void exigirVersion(long actual, Long recibida) {
        if (recibida == null || actual != recibida) {
            throw new ConflictException(
                    "VERSION_DESACTUALIZADA", "La solicitud fue modificada; vuelva a cargarla");
        }
    }
}
