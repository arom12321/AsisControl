package com.asiscontrol.service;

import com.asiscontrol.dto.matricula.MatriculaDtos.ActualizarMatriculaRequest;
import com.asiscontrol.dto.matricula.MatriculaDtos.CrearMatriculaRequest;
import com.asiscontrol.dto.matricula.MatriculaDtos.MatriculaResponse;
import com.asiscontrol.entity.Matricula;
import com.asiscontrol.entity.Seccion;
import com.asiscontrol.entity.SolicitudMatricula;
import com.asiscontrol.entity.enums.EstadoMatricula;
import com.asiscontrol.entity.enums.EstadoSolicitudMatricula;
import com.asiscontrol.exception.BusinessRuleException;
import com.asiscontrol.exception.ConflictException;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.MatriculaRepository;
import com.asiscontrol.repository.SeccionRepository;
import com.asiscontrol.repository.SolicitudMatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final SolicitudMatriculaRepository solicitudRepository;
    private final SeccionRepository seccionRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            SolicitudMatriculaRepository solicitudRepository,
            SeccionRepository seccionRepository) {
        this.matriculaRepository = matriculaRepository;
        this.solicitudRepository = solicitudRepository;
        this.seccionRepository = seccionRepository;
    }

    @Transactional
    public MatriculaResponse crear(CrearMatriculaRequest request) {
        SolicitudMatricula solicitud = solicitudRepository
                .buscarActivaParaActualizar(request.solicitudMatriculaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SOLICITUD_MATRICULA_NO_ENCONTRADA",
                        "No existe la solicitud de matricula indicada"));
        exigirVersion(solicitud.getVersion(), request.versionSolicitud(), "solicitud");
        if (solicitud.getEstado() != EstadoSolicitudMatricula.EN_REVISION) {
            throw new BusinessRuleException(
                    "SOLICITUD_NO_FINALIZABLE",
                    "La solicitud debe estar en revision para crear la matricula");
        }
        if (matriculaRepository.findBySolicitudMatriculaIdAndActivoTrue(solicitud.getId()).isPresent()) {
            throw new ConflictException(
                    "SOLICITUD_YA_FINALIZADA", "La solicitud ya genero una matricula");
        }

        Seccion seccion = seccionRepository.buscarActivaParaActualizar(solicitud.getSeccion().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SECCION_NO_ENCONTRADA", "La seccion de la solicitud ya no esta disponible"));
        long ocupados = matriculaRepository.countBySeccionIdAndEstadoAndActivoTrue(
                seccion.getId(), EstadoMatricula.ACTIVA);
        if (ocupados >= seccion.getCapacidadMaxima()) {
            throw new BusinessRuleException(
                    "SECCION_SIN_VACANTES", "La seccion ya alcanzo su capacidad maxima");
        }
        if (matriculaRepository.existsByAlumnoIdAndAnioAcademico(
                solicitud.getAlumno().getId(), seccion.getAnioAcademico())) {
            throw new ConflictException(
                    "ALUMNO_YA_MATRICULADO",
                    "El alumno ya tiene una matricula para ese anio academico");
        }

        Instant ahora = Instant.now();
        Matricula matricula = new Matricula();
        matricula.setCodigoMatricula(generarCodigo(seccion.getAnioAcademico()));
        matricula.setSolicitudMatricula(solicitud);
        matricula.setAlumno(solicitud.getAlumno());
        matricula.setSeccion(seccion);
        matricula.setAnioAcademico(seccion.getAnioAcademico());
        matricula.setFechaMatricula(ahora);
        matricula.setEstado(EstadoMatricula.ACTIVA);
        matricula.setActivo(true);

        solicitud.setEstado(EstadoSolicitudMatricula.MATRICULA_FINALIZADA);
        solicitud.setFechaRevision(ahora);
        solicitudRepository.save(solicitud);
        return mapear(matriculaRepository.save(matricula));
    }

    @Transactional
    public MatriculaResponse actualizar(Long id, ActualizarMatriculaRequest request) {
        Matricula matricula = obtenerEntidad(id);
        exigirVersion(matricula.getVersion(), request.version(), "matricula");
        if (matricula.getEstado() == request.estado()) {
            return mapear(matricula);
        }
        if (matricula.getEstado() != EstadoMatricula.ACTIVA
                || (request.estado() != EstadoMatricula.RETIRADA
                    && request.estado() != EstadoMatricula.FINALIZADA
                    && request.estado() != EstadoMatricula.ANULADA)) {
            throw new BusinessRuleException(
                    "MATRICULA_TRANSICION_INVALIDA",
                    "No se permite pasar de " + matricula.getEstado() + " a " + request.estado());
        }
        matricula.setEstado(request.estado());
        return mapear(matriculaRepository.save(matricula));
    }

    public MatriculaResponse obtener(Long id) {
        return mapear(obtenerEntidad(id));
    }

    public Page<MatriculaResponse> listar(
            Long personaAlumnoId,
            Long personaApoderadoId,
            Pageable pageable
    ) {
        return matriculaRepository.buscarVisibles(
                        personaAlumnoId,
                        personaApoderadoId,
                        pageable)
                .map(this::mapear);
    }

    @Transactional
    public void eliminar(Long id) {
        Matricula matricula = obtenerEntidad(id);
        if (matricula.getEstado() == EstadoMatricula.ACTIVA) {
            throw new BusinessRuleException(
                    "MATRICULA_NO_ELIMINABLE",
                    "Una matricula activa debe retirarse, finalizarse o anularse antes de eliminarse");
        }
        matricula.setActivo(false);
        matriculaRepository.save(matricula);
    }

    private Matricula obtenerEntidad(Long id) {
        return matriculaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MATRICULA_NO_ENCONTRADA", "No existe una matricula activa con id " + id));
    }

    private String generarCodigo(int anio) {
        return "MAT-" + anio + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private MatriculaResponse mapear(Matricula matricula) {
        return new MatriculaResponse(
                matricula.getId(), matricula.getCodigoMatricula(),
                matricula.getSolicitudMatricula().getId(), matricula.getAlumno().getId(),
                matricula.getAlumno().getCodigoAlumno(), matricula.getAlumno().getPersona().nombreCompleto(),
                matricula.getSeccion().getId(), matricula.getSeccion().getNombre(),
                matricula.getSeccion().getGrado(), matricula.getAnioAcademico(),
                matricula.getFechaMatricula(), matricula.getEstado(), matricula.isActivo(),
                matricula.getVersion());
    }

    private void exigirVersion(long actual, Long recibida, String recurso) {
        if (recibida == null || actual != recibida) {
            throw new ConflictException(
                    "VERSION_DESACTUALIZADA", "La " + recurso + " fue modificada; vuelva a cargarla");
        }
    }
}
