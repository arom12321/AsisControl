package com.asiscontrol.service;

import com.asiscontrol.dto.academico.AcademicoDtos.ActualizarSeccionRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.CrearSeccionRequest;
import com.asiscontrol.dto.academico.AcademicoDtos.SeccionResponse;
import com.asiscontrol.entity.Docente;
import com.asiscontrol.entity.Seccion;
import com.asiscontrol.entity.enums.EstadoMatricula;
import com.asiscontrol.exception.BusinessRuleException;
import com.asiscontrol.exception.ConflictException;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.DocenteRepository;
import com.asiscontrol.repository.MatriculaRepository;
import com.asiscontrol.repository.SeccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SeccionService {

    private final SeccionRepository seccionRepository;
    private final DocenteRepository docenteRepository;
    private final MatriculaRepository matriculaRepository;

    public SeccionService(
            SeccionRepository seccionRepository,
            DocenteRepository docenteRepository,
            MatriculaRepository matriculaRepository) {
        this.seccionRepository = seccionRepository;
        this.docenteRepository = docenteRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public SeccionResponse crear(CrearSeccionRequest request) {
        validarUnica(request.anioAcademico(), request.grado(), request.nombre(), null);
        Seccion seccion = new Seccion();
        aplicar(seccion, request.nombre(), request.grado(), request.anioAcademico(),
                request.capacidadMaxima(), request.docenteTutorId());
        seccion.setActivo(true);
        return mapear(seccionRepository.save(seccion));
    }

    @Transactional
    public SeccionResponse actualizar(Long id, ActualizarSeccionRequest request) {
        Seccion seccion = obtenerEntidad(id);
        exigirVersion(seccion.getVersion(), request.version());
        validarUnica(request.anioAcademico(), request.grado(), request.nombre(), id);
        long matriculados = matriculaRepository.countBySeccionIdAndEstadoAndActivoTrue(
                id, EstadoMatricula.ACTIVA);
        if (request.capacidadMaxima() < matriculados) {
            throw new BusinessRuleException(
                    "SECCION_CAPACIDAD_INFERIOR_A_MATRICULADOS",
                    "La capacidad no puede ser menor que los alumnos matriculados");
        }
        aplicar(seccion, request.nombre(), request.grado(), request.anioAcademico(),
                request.capacidadMaxima(), request.docenteTutorId());
        return mapear(seccionRepository.save(seccion));
    }

    public SeccionResponse obtener(Long id) {
        return mapear(obtenerEntidad(id));
    }

    public Page<SeccionResponse> listar(Pageable pageable) {
        return seccionRepository.findByActivoTrue(pageable).map(this::mapear);
    }

    @Transactional
    public void eliminar(Long id) {
        Seccion seccion = obtenerEntidad(id);
        if (matriculaRepository.countBySeccionIdAndEstadoAndActivoTrue(id, EstadoMatricula.ACTIVA) > 0) {
            throw new ConflictException(
                    "SECCION_CON_MATRICULAS", "No se puede desactivar una seccion con matriculas activas");
        }
        seccion.setActivo(false);
        seccionRepository.save(seccion);
    }

    public Seccion obtenerEntidad(Long id) {
        return seccionRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SECCION_NO_ENCONTRADA", "No existe una seccion activa con id " + id));
    }

    private void validarUnica(int anio, int grado, String nombre, Long idActual) {
        seccionRepository.findByAnioAcademicoAndGradoAndNombreIgnoreCaseAndActivoTrue(
                        anio, grado, nombre.trim())
                .filter(existente -> idActual == null || !existente.getId().equals(idActual))
                .ifPresent(existente -> {
                    throw new ConflictException(
                            "SECCION_DUPLICADA", "Ya existe esa seccion para el grado y anio academico");
                });
    }

    private void aplicar(
            Seccion seccion,
            String nombre,
            int grado,
            int anioAcademico,
            int capacidadMaxima,
            Long docenteTutorId) {
        seccion.setNombre(nombre.trim().toUpperCase());
        seccion.setGrado(grado);
        seccion.setAnioAcademico(anioAcademico);
        seccion.setCapacidadMaxima(capacidadMaxima);
        seccion.setDocenteTutor(docenteTutorId == null ? null : buscarDocente(docenteTutorId));
    }

    private Docente buscarDocente(Long id) {
        return docenteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DOCENTE_NO_ENCONTRADO", "No existe un docente activo con id " + id));
    }

    private SeccionResponse mapear(Seccion seccion) {
        Docente tutor = seccion.getDocenteTutor();
        return new SeccionResponse(
                seccion.getId(), seccion.getNombre(), seccion.getGrado(), seccion.getAnioAcademico(),
                seccion.getCapacidadMaxima(), tutor == null ? null : tutor.getId(),
                tutor == null ? null : tutor.getPersona().nombreCompleto(), seccion.isActivo(),
                seccion.getVersion());
    }

    private void exigirVersion(long actual, Long recibida) {
        if (recibida == null || actual != recibida) {
            throw new ConflictException(
                    "VERSION_DESACTUALIZADA", "La seccion fue modificada; vuelva a cargarla");
        }
    }
}
