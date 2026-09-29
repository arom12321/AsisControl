package com.asiscontrol.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ScopedRepositoryIntegrationTest {

    @Autowired
    private AsistenciaAlumnoRepository asistenciaAlumnoRepository;
    @Autowired
    private AsistenciaDocenteRepository asistenciaDocenteRepository;
    @Autowired
    private JustificacionAsistenciaRepository justificacionRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private EntregaTareaRepository entregaRepository;
    @Autowired
    private EvaluacionRepository evaluacionRepository;
    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private SolicitudMatriculaRepository solicitudRepository;
    @Autowired
    private CompromisoPagoRepository compromisoRepository;
    @Autowired
    private TransaccionPagoRepository transaccionRepository;
    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private AlertaRiesgoRepository alertaRiesgoRepository;

    @Test
    void consultasGlobalesYRestringidasAceptanFiltrosNulos() {
        PageRequest pagina = PageRequest.of(0, 10);

        assertThat(asistenciaAlumnoRepository.buscarActivas(
                null, null, null, null, null, null, pagina)).isEmpty();
        assertThat(asistenciaDocenteRepository.buscarVisibles(null, null, pagina)).isEmpty();
        assertThat(justificacionRepository.buscarVisibles(
                null, null, null, null, pagina)).isEmpty();
        assertThat(tareaRepository.buscarVisibles(
                null, null, null, null, pagina)).isEmpty();
        assertThat(entregaRepository.buscarVisibles(
                1L, null, null, null, pagina)).isEmpty();
        assertThat(evaluacionRepository.buscar(
                null, null, null, null, null, null, pagina)).isEmpty();
        assertThat(calificacionRepository.buscar(
                null, null, null, null, pagina)).isEmpty();
        assertThat(matriculaRepository.buscarVisibles(null, null, pagina)).isEmpty();
        assertThat(solicitudRepository.buscarVisibles(null, null, pagina)).isEmpty();
        assertThat(compromisoRepository.buscar(
                null, null, null, null, pagina)).isEmpty();
        assertThat(transaccionRepository.buscarVisibles(null, null, pagina)).isEmpty();
        assertThat(comprobanteRepository.buscarVisibles(null, pagina)).isEmpty();
        assertThat(alertaRiesgoRepository.buscar(
                null, null, null, null, pagina)).isEmpty();
    }
}
