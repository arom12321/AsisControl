package com.asiscontrol.controller;

import com.asiscontrol.dto.persona.PersonaDtos;
import com.asiscontrol.entity.enums.DiaSemana;
import com.asiscontrol.entity.enums.Especialidad;
import com.asiscontrol.entity.enums.EstadoAsistencia;
import com.asiscontrol.entity.enums.EstadoSolicitudMatricula;
import com.asiscontrol.entity.enums.MedioPago;
import com.asiscontrol.entity.enums.Mes;
import com.asiscontrol.entity.enums.NivelLogro;
import com.asiscontrol.entity.enums.Parentesco;
import com.asiscontrol.entity.enums.Sexo;
import com.asiscontrol.entity.enums.TipoAula;
import com.asiscontrol.entity.enums.TipoDocumento;
import com.asiscontrol.entity.enums.TipoEvaluacion;
import com.asiscontrol.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final PersonaService personaService;

    public CatalogoController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/nacionalidades")
    public List<PersonaDtos.NacionalidadResponse> nationalities() {
        return personaService.listNationalities();
    }

    @PostMapping("/nacionalidades")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public PersonaDtos.NacionalidadResponse createNationality(
            @Valid @RequestBody PersonaDtos.NacionalidadRequest request
    ) {
        return personaService.createNationality(request);
    }

    @GetMapping("/enumeraciones")
    public Map<String, Object> enumerations() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("tipoDocumento", TipoDocumento.values());
        values.put("sexo", Sexo.values());
        values.put("especialidad", Especialidad.values());
        values.put("parentesco", Parentesco.values());
        values.put("tipoAula", TipoAula.values());
        values.put("diaSemana", DiaSemana.values());
        values.put("mes", Mes.values());
        values.put("estadoAsistencia", EstadoAsistencia.values());
        values.put("tipoEvaluacion", TipoEvaluacion.values());
        values.put("nivelLogro", NivelLogro.values());
        values.put("estadoSolicitudMatricula", EstadoSolicitudMatricula.values());
        values.put("medioPago", MedioPago.values());
        return values;
    }
}
