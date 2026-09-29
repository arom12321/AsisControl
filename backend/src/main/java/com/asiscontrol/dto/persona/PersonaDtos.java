package com.asiscontrol.dto.persona;

import com.asiscontrol.entity.enums.Especialidad;
import com.asiscontrol.entity.enums.Parentesco;
import com.asiscontrol.entity.enums.Sexo;
import com.asiscontrol.entity.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class PersonaDtos {

    private PersonaDtos() {
    }

    public record PersonaRequest(
            @NotBlank(message = "Los nombres son obligatorios")
            @Size(max = 100)
            String nombres,
            @NotBlank(message = "El apellido paterno es obligatorio")
            @Size(max = 80)
            String apellidoPaterno,
            @NotBlank(message = "El apellido materno es obligatorio")
            @Size(max = 80)
            String apellidoMaterno,
            @NotNull(message = "El tipo de documento es obligatorio")
            TipoDocumento tipoDocumento,
            @NotBlank(message = "El numero de documento es obligatorio")
            @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "El documento no tiene un formato valido")
            String numeroDocumento,
            Long nacionalidadId,
            @NotNull(message = "El sexo es obligatorio")
            Sexo sexo,
            @NotNull(message = "La fecha de nacimiento es obligatoria")
            @Past(message = "La fecha de nacimiento debe estar en el pasado")
            LocalDate fechaNacimiento,
            @Pattern(regexp = "[0-9+() -]{7,20}", message = "El telefono no tiene un formato valido")
            String telefono,
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato valido")
            String correo,
            @Size(max = 250)
            String direccion
    ) {
    }

    public record PersonaResponse(
            Long id,
            String nombres,
            String apellidoPaterno,
            String apellidoMaterno,
            String nombreCompleto,
            TipoDocumento tipoDocumento,
            String numeroDocumento,
            Long nacionalidadId,
            String nacionalidad,
            Sexo sexo,
            LocalDate fechaNacimiento,
            String telefono,
            String correo,
            String direccion,
            boolean activo,
            Instant fechaCreacion,
            Instant fechaActualizacion
    ) {
    }

    public record AlumnoCreateRequest(
            @NotNull @Valid PersonaRequest persona,
            @NotBlank(message = "El codigo del alumno es obligatorio")
            @Size(max = 30)
            String codigoAlumno
    ) {
    }

    public record AlumnoUpdateRequest(
            @NotNull @Valid PersonaRequest persona,
            @NotBlank(message = "El codigo del alumno es obligatorio")
            @Size(max = 30)
            String codigoAlumno
    ) {
    }

    public record AlumnoResponse(
            Long id,
            String codigoAlumno,
            PersonaResponse persona,
            List<ApoderadoVinculadoResponse> apoderados,
            boolean activo
    ) {
    }

    public record DocenteCreateRequest(
            @NotNull @Valid PersonaRequest persona,
            @NotBlank(message = "El codigo del docente es obligatorio")
            @Size(max = 30)
            String codigoDocente,
            @NotNull(message = "La fecha de ingreso es obligatoria")
            LocalDate fechaIngreso,
            @NotNull(message = "La especialidad es obligatoria")
            Especialidad especialidad
    ) {
    }

    public record DocenteUpdateRequest(
            @NotNull @Valid PersonaRequest persona,
            @NotBlank(message = "El codigo del docente es obligatorio")
            @Size(max = 30)
            String codigoDocente,
            @NotNull(message = "La fecha de ingreso es obligatoria")
            LocalDate fechaIngreso,
            @NotNull(message = "La especialidad es obligatoria")
            Especialidad especialidad
    ) {
    }

    public record DocenteResponse(
            Long id,
            String codigoDocente,
            LocalDate fechaIngreso,
            Especialidad especialidad,
            PersonaResponse persona,
            boolean activo
    ) {
    }

    public record ApoderadoCreateRequest(@NotNull @Valid PersonaRequest persona) {
    }

    public record ApoderadoUpdateRequest(@NotNull @Valid PersonaRequest persona) {
    }

    public record ApoderadoResponse(
            Long id,
            PersonaResponse persona,
            List<AlumnoVinculadoResponse> alumnos,
            boolean activo
    ) {
    }

    public record VinculoApoderadoRequest(
            @NotNull(message = "El apoderado es obligatorio")
            Long apoderadoId,
            @NotNull(message = "El parentesco es obligatorio")
            Parentesco parentesco,
            boolean principal
    ) {
    }

    public record ApoderadoVinculadoResponse(
            Long id,
            Long apoderadoId,
            String nombreCompleto,
            Parentesco parentesco,
            boolean principal
    ) {
    }

    public record AlumnoVinculadoResponse(
            Long id,
            Long alumnoId,
            String codigoAlumno,
            String nombreCompleto,
            Parentesco parentesco,
            boolean principal
    ) {
    }

    public record ImportIssue(int row, String field, String message) {
    }

    public record ImportResult(
            int totalRows,
            int importedRows,
            int rejectedRows,
            List<ImportIssue> issues
    ) {
    }

    public record NacionalidadRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 80)
            String nombre
    ) {
    }

    public record NacionalidadResponse(Long id, String nombre) {
    }
}
