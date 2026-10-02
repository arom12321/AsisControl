package com.asiscontrol.service;

import com.asiscontrol.dto.academico.AcademicoDtos.ActualizarAsignacionCursoRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.AsignacionCursoResponse;
import com.asiscontrol.dto.academico.AcademicoDtos.CrearAsignacionCursoRequest;
import com.asiscontrol.entity.AsignacionCurso;
import com.asiscontrol.entity.Curso;
import com.asiscontrol.entity.Docente;
import com.asiscontrol.entity.Seccion;
import com.asiscontrol.exception.ConflictException;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.AsignacionCursoRepository;
import com.asiscontrol.repository.CursoRepository;
import com.asiscontrol.repository.DocenteRepository;
import com.asiscontrol.repository.SeccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AsignacionCursoService {

    private final AsignacionCursoRepository asignacionRepository;
    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;
    private final DocenteRepository docenteRepository;

    public AsignacionCursoService(
            AsignacionCursoRepository asignacionRepository,
            CursoRepository cursoRepository,
            SeccionRepository seccionRepository,
            DocenteRepository docenteRepository) {
        this.asignacionRepository = asignacionRepository;
        this.cursoRepository = cursoRepository;
        this.seccionRepository = seccionRepository;
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public AsignacionCursoResponse crear(CrearAsignacionCursoRequest request) {
        validarUnica(request.cursoId(), request.seccionId(), null);
        AsignacionCurso asignacion = new AsignacionCurso();
        asignacion.setCurso(buscarCurso(request.cursoId()));
        asignacion.setSeccion(buscarSeccion(request.seccionId()));
        asignacion.setDocente(buscarDocente(request.docenteId()));
        asignacion.setActivo(true);
        return mapear(asignacionRepository.save(asignacion));
    }

    @Transactional
    public AsignacionCursoResponse actualizar(Long id, ActualizarAsignacionCursoRequest request) {
        AsignacionCurso asignacion = obtenerEntidad(id);
        exigirVersion(asignacion.getVersion(), request.version());
        validarUnica(request.cursoId(), request.seccionId(), id);
        asignacion.setCurso(buscarCurso(request.cursoId()));
        asignacion.setSeccion(buscarSeccion(request.seccionId()));
        asignacion.setDocente(buscarDocente(request.docenteId()));
        return mapear(asignacionRepository.save(asignacion));
    }

    public AsignacionCursoResponse obtener(Long id) {
        return mapear(obtenerEntidad(id));
    }

    public Page<AsignacionCursoResponse> listar(Pageable pageable) {
        return asignacionRepository.findByActivoTrue(pageable).map(this::mapear);
    }

    @Transactional
    public void eliminar(Long id) {
        AsignacionCurso asignacion = obtenerEntidad(id);
        asignacion.setActivo(false);
        asignacionRepository.save(asignacion);
    }

    public AsignacionCurso obtenerEntidad(Long id) {
        return asignacionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ASIGNACION_CURSO_NO_ENCONTRADA",
                        "No existe una asignacion de curso activa con id " + id));
    }

    private Curso buscarCurso(Long id) {
        return cursoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CURSO_NO_ENCONTRADO", "No existe un curso activo con id " + id));
    }

    private Seccion buscarSeccion(Long id) {
        return seccionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SECCION_NO_ENCONTRADA", "No existe una seccion activa con id " + id));
    }

    private Docente buscarDocente(Long id) {
        return docenteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DOCENTE_NO_ENCONTRADO", "No existe un docente activo con id " + id));
    }

    private void validarUnica(Long cursoId, Long seccionId, Long idActual) {
        asignacionRepository.findByCursoIdAndSeccionIdAndActivoTrue(cursoId, seccionId)
                .filter(existente -> idActual == null || !existente.getId().equals(idActual))
                .ifPresent(existente -> {
                    throw new ConflictException(
                            "ASIGNACION_CURSO_DUPLICADA", "El curso ya esta asignado a la seccion");
                });
    }

    private AsignacionCursoResponse mapear(AsignacionCurso asignacion) {
        return new AsignacionCursoResponse(
                asignacion.getId(), asignacion.getCurso().getId(), asignacion.getCurso().getNombre(),
                asignacion.getSeccion().getId(), asignacion.getSeccion().getNombre(),
                asignacion.getSeccion().getAnioAcademico(), asignacion.getDocente().getId(),
                asignacion.getDocente().getPersona().nombreCompleto(), asignacion.isActivo(),
                asignacion.getVersion());
    }

    private void exigirVersion(long actual, Long recibida) {
        if (recibida == null || actual != recibida) {
            throw new ConflictException(
                    "VERSION_DESACTUALIZADA", "La asignacion fue modificada; vuelva a cargarla");
        }
    }
}
