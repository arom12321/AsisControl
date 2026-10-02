-- ASISCONTROL - esquema base administrado por Flyway.
-- Esta migracion crea las 43 tablas cuando el esquema esta vacio.
-- En una base preexistente se omite mediante baseline-on-migrate con version 1.
-- No contiene credenciales, datos iniciales ni operaciones destructivas.

CREATE TABLE AccesoUsuario (
    id_acceso_usuario BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    bloqueado_hasta DATETIME(6),
    contrasena_hash VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    estado_acceso ENUM ('ACTIVO','BLOQUEADO_TEMPORALMENTE','DESACTIVADO','PENDIENTE') NOT NULL,
    intentos_fallidos INTEGER NOT NULL,
    requiere_cambio_contrasena BIT NOT NULL,
    ultimo_acceso DATETIME(6),
    username VARCHAR(80) NOT NULL,
    id_persona BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_acceso_usuario)
) ENGINE=InnoDB;

CREATE TABLE Administrador (
    id_administrador BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    id_persona BIGINT NOT NULL,
    PRIMARY KEY (id_administrador)
) ENGINE=InnoDB;

CREATE TABLE AlertaRiesgo (
    id_alerta_riesgo BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    estado_alerta ENUM ('ACTIVA','EN_SEGUIMIENTO','RESUELTA') NOT NULL,
    fecha_atencion DATETIME(6),
    fecha_deteccion DATETIME(6) NOT NULL,
    mensaje VARCHAR(600) NOT NULL,
    nivel_riesgo ENUM ('ALTO','BAJO','MEDIO') NOT NULL,
    observacion VARCHAR(1000),
    valor_detectado DECIMAL(12,4) NOT NULL,
    id_alumno BIGINT NOT NULL,
    id_criterio_riesgo BIGINT NOT NULL,
    PRIMARY KEY (id_alerta_riesgo)
) ENGINE=InnoDB;

CREATE TABLE Alumno (
    id_alumno BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo_alumno VARCHAR(30) NOT NULL,
    id_persona BIGINT NOT NULL,
    PRIMARY KEY (id_alumno)
) ENGINE=InnoDB;

CREATE TABLE AlumnoApoderado (
    id_alumno_apoderado BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    parentesco ENUM ('MADRE','OTRO_FAMILIAR','PADRE','TUTOR_LEGAL') NOT NULL,
    es_principal BIT NOT NULL,
    id_alumno BIGINT NOT NULL,
    id_apoderado BIGINT NOT NULL,
    PRIMARY KEY (id_alumno_apoderado)
) ENGINE=InnoDB;

CREATE TABLE Apoderado (
    id_apoderado BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    id_persona BIGINT NOT NULL,
    PRIMARY KEY (id_apoderado)
) ENGINE=InnoDB;

CREATE TABLE ArchivoAdjunto (
    id_archivo BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    nombre_almacenado VARCHAR(80) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    sha_256 VARCHAR(64) NOT NULL,
    tamanio_bytes BIGINT NOT NULL,
    tipo_mime VARCHAR(120) NOT NULL,
    id_persona_carga BIGINT NOT NULL,
    PRIMARY KEY (id_archivo)
) ENGINE=InnoDB;

CREATE TABLE AsignacionCurso (
    id_asignacion_curso BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    id_docente BIGINT NOT NULL,
    id_seccion BIGINT NOT NULL,
    PRIMARY KEY (id_asignacion_curso)
) ENGINE=InnoDB;

CREATE TABLE AsistenciaAlumno (
    id_asistencia_alumno BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    comentario VARCHAR(1000),
    estado_asistencia ENUM ('INASISTENCIA','INASISTENCIA_JUSTIFICADA','PRESENTE','SIN_MARCACION','TARDANZA','TARDANZA_JUSTIFICADA') NOT NULL,
    fecha_registro DATE NOT NULL,
    hora_registro TIME(0) NOT NULL,
    id_alumno BIGINT NOT NULL,
    id_asignacion_curso BIGINT NOT NULL,
    id_docente_registrador BIGINT,
    PRIMARY KEY (id_asistencia_alumno)
) ENGINE=InnoDB;

CREATE TABLE AsistenciaDocente (
    id_asistencia_docente BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    es_contingencia BIT NOT NULL,
    estado_asistencia ENUM ('INASISTENCIA','INASISTENCIA_JUSTIFICADA','PRESENTE','SIN_MARCACION','TARDANZA','TARDANZA_JUSTIFICADA') NOT NULL,
    fecha_hora_marcada DATETIME(6) NOT NULL,
    fecha_jornada DATE NOT NULL,
    motivo_contingencia VARCHAR(1000),
    id_codigo_qr BIGINT,
    id_docente BIGINT NOT NULL,
    PRIMARY KEY (id_asistencia_docente)
) ENGINE=InnoDB;

CREATE TABLE Aula (
    id_aula BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    capacidad INTEGER NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo_aula ENUM ('AULA_REGULAR','BIBLIOTECA','LABORATORIO_CIENCIAS','LABORATORIO_COMPUTO','SALA_USOS_MULTIPLES','TALLER') NOT NULL,
    ubicacion VARCHAR(180) NOT NULL,
    PRIMARY KEY (id_aula)
) ENGINE=InnoDB;

CREATE TABLE Calificacion (
    id_calificacion BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    fecha_registro DATETIME(6) NOT NULL,
    nivel_logro ENUM ('A','AD','B','C') NOT NULL,
    observacion VARCHAR(1000),
    id_alumno BIGINT NOT NULL,
    id_competencia BIGINT NOT NULL,
    id_evaluacion BIGINT NOT NULL,
    PRIMARY KEY (id_calificacion)
) ENGINE=InnoDB;

CREATE TABLE CategoriaPermiso (
    id_categoria_permiso BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    descripcion VARCHAR(250),
    nombre VARCHAR(80) NOT NULL,
    PRIMARY KEY (id_categoria_permiso)
) ENGINE=InnoDB;

CREATE TABLE CodigoQRAsistencia (
    id_codigo_qr BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    estado_codigo_qr ENUM ('EXPIRADO','INVALIDADO','UTILIZADO','VIGENTE') NOT NULL,
    fecha_hora_cancelacion DATETIME(6),
    fecha_hora_emision DATETIME(6) NOT NULL,
    fecha_hora_expiracion DATETIME(6) NOT NULL,
    fecha_hora_uso DATETIME(6),
    token_hash VARCHAR(64) NOT NULL,
    id_docente BIGINT NOT NULL,
    PRIMARY KEY (id_codigo_qr)
) ENGINE=InnoDB;

CREATE TABLE Competencia (
    id_competencia BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(700),
    nombre VARCHAR(180) NOT NULL,
    PRIMARY KEY (id_competencia)
) ENGINE=InnoDB;

CREATE TABLE Comprobante (
    id_comprobante BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    fecha_emision DATETIME(6) NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    numero VARCHAR(30) NOT NULL,
    serie VARCHAR(20) NOT NULL,
    tipo_comprobante ENUM ('BOLETA','CONSTANCIA_PAGO_DIGITAL','CONSTANCIA_TRANSFERENCIA','FACTURA','RECIBO') NOT NULL,
    id_archivo_adjunto BIGINT,
    id_transaccion_pago BIGINT NOT NULL,
    PRIMARY KEY (id_comprobante)
) ENGINE=InnoDB;

CREATE TABLE CompromisoPago (
    id_compromiso_pago BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo_compromiso VARCHAR(50) NOT NULL,
    concepto VARCHAR(160) NOT NULL,
    descripcion VARCHAR(600),
    estado_compromiso ENUM ('ANULADO','PAGADO','PARCIAL','PENDIENTE','VENCIDO') NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    monto_pagado DECIMAL(12,2) NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    id_matricula BIGINT NOT NULL,
    PRIMARY KEY (id_compromiso_pago)
) ENGINE=InnoDB;

CREATE TABLE CriterioRiesgo (
    id_criterio_riesgo BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    descripcion VARCHAR(600),
    indicador ENUM ('COMPETENCIAS_EN_INICIO','INASISTENCIAS','PROMEDIO_ACADEMICO','TARDANZAS','TAREAS_NO_ENTREGADAS') NOT NULL,
    nivel_riesgo ENUM ('ALTO','BAJO','MEDIO') NOT NULL,
    nombre VARCHAR(160) NOT NULL,
    operador ENUM ('IGUAL','MAYOR_IGUAL','MAYOR_QUE','MENOR_IGUAL','MENOR_QUE') NOT NULL,
    umbral DECIMAL(12,4) NOT NULL,
    PRIMARY KEY (id_criterio_riesgo)
) ENGINE=InnoDB;

CREATE TABLE Curso (
    id_curso BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500),
    horas_semanales INTEGER NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    PRIMARY KEY (id_curso)
) ENGINE=InnoDB;

CREATE TABLE CursoCompetencia (
    id_curso_competencia BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    orden INTEGER NOT NULL,
    id_competencia BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    PRIMARY KEY (id_curso_competencia)
) ENGINE=InnoDB;

CREATE TABLE Docente (
    id_docente BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo_docente VARCHAR(30) NOT NULL,
    especialidad ENUM ('ARTE_Y_CULTURA','CIENCIAS_SOCIALES','CIENCIA_Y_TECNOLOGIA','COMUNICACION','EDUCACION_FISICA','INGLES','MATEMATICA','TUTORIA') NOT NULL,
    fecha_ingreso DATE NOT NULL,
    id_persona BIGINT NOT NULL,
    PRIMARY KEY (id_docente)
) ENGINE=InnoDB;

CREATE TABLE EntregaTarea (
    id_entrega_tarea BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    comentario VARCHAR(2000),
    estado_entrega ENUM ('ANULADO','CALIFICADO','ENTREGADO','ENTREGADO_TARDE','PENDIENTE') NOT NULL,
    fecha_hora_entrega DATETIME(6) NOT NULL,
    nota INTEGER,
    id_alumno BIGINT NOT NULL,
    id_tarea BIGINT NOT NULL,
    PRIMARY KEY (id_entrega_tarea)
) ENGINE=InnoDB;

CREATE TABLE EntregaTareaArchivo (
    id_entrega_tarea BIGINT NOT NULL,
    id_archivo BIGINT NOT NULL,
    PRIMARY KEY (id_entrega_tarea, id_archivo)
) ENGINE=InnoDB;

CREATE TABLE Evaluacion (
    id_evaluacion BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    descripcion VARCHAR(1000),
    estado_evaluacion ENUM ('ANULADA','BORRADOR','CERRADA','PUBLICADA') NOT NULL,
    fecha_cierre DATETIME(6),
    fecha_evaluacion DATE NOT NULL,
    fecha_publicacion DATETIME(6),
    periodo_evaluacion ENUM ('ANUAL','BIMESTRAL','DIARIO','MENSUAL','PERIODO_ACADEMICO','SEMANAL','TRIMESTRAL') NOT NULL,
    ponderacion_porcentaje DECIMAL(5,2) NOT NULL,
    tipo_evaluacion ENUM ('EVALUACION_ORAL','EXAMEN','PRACTICA_CALIFICADA','PROYECTO','TRABAJO_ACADEMICO') NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    id_asignacion_curso BIGINT NOT NULL,
    PRIMARY KEY (id_evaluacion)
) ENGINE=InnoDB;

CREATE TABLE EvaluacionCompetencia (
    id_evaluacion BIGINT NOT NULL,
    id_competencia BIGINT NOT NULL,
    PRIMARY KEY (id_evaluacion, id_competencia)
) ENGINE=InnoDB;

CREATE TABLE Horario (
    id_horario BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    dia_semana ENUM ('JUEVES','LUNES','MARTES','MIERCOLES','SABADO','VIERNES') NOT NULL,
    hora_fin TIME(0) NOT NULL,
    hora_inicio TIME(0) NOT NULL,
    id_asignacion_curso BIGINT NOT NULL,
    id_aula BIGINT NOT NULL,
    PRIMARY KEY (id_horario)
) ENGINE=InnoDB;

CREATE TABLE JustificacionAsistencia (
    id_justificacion_asistencia BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    estado_justificacion ENUM ('APROBADA','CANCELADA','ENVIADA','EN_REVISION','OBSERVADA','RECHAZADA') NOT NULL,
    fecha_hora_envio DATETIME(6) NOT NULL,
    fecha_hora_revision DATETIME(6),
    motivo VARCHAR(2000) NOT NULL,
    observacion_revision VARCHAR(2000),
    id_asistencia_alumno BIGINT NOT NULL,
    id_docente_revisor BIGINT,
    PRIMARY KEY (id_justificacion_asistencia)
) ENGINE=InnoDB;

CREATE TABLE JustificacionAsistenciaArchivo (
    id_justificacion_asistencia BIGINT NOT NULL,
    id_archivo BIGINT NOT NULL,
    PRIMARY KEY (id_justificacion_asistencia, id_archivo)
) ENGINE=InnoDB;

CREATE TABLE Matricula (
    id_matricula BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    anio_academico INTEGER NOT NULL,
    codigo_matricula VARCHAR(50) NOT NULL,
    estado_matricula ENUM ('ACTIVA','ANULADA','FINALIZADA','RETIRADA') NOT NULL,
    fecha_matricula DATETIME(6) NOT NULL,
    id_alumno BIGINT NOT NULL,
    id_seccion BIGINT NOT NULL,
    id_solicitud_matricula BIGINT NOT NULL,
    PRIMARY KEY (id_matricula)
) ENGINE=InnoDB;

CREATE TABLE Nacionalidad (
    id_nacionalidad BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    PRIMARY KEY (id_nacionalidad)
) ENGINE=InnoDB;

CREATE TABLE Notificacion (
    id_notificacion BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    fecha_lectura DATETIME(6),
    leida BIT NOT NULL,
    mensaje VARCHAR(1000) NOT NULL,
    tipo_notificacion ENUM ('ACADEMICA','ALERTA_RIESGO','ASISTENCIA','INFORMACION','PAGO','SISTEMA') NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    url_destino VARCHAR(300),
    id_acceso_usuario BIGINT NOT NULL,
    PRIMARY KEY (id_notificacion)
) ENGINE=InnoDB;

CREATE TABLE Permiso (
    id_permiso BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    codigo VARCHAR(80) NOT NULL,
    descripcion VARCHAR(250),
    nombre VARCHAR(120) NOT NULL,
    id_categoria_permiso BIGINT,
    PRIMARY KEY (id_permiso)
) ENGINE=InnoDB;

CREATE TABLE Persona (
    id_persona BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    apellido_materno VARCHAR(80) NOT NULL,
    apellido_paterno VARCHAR(80) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    direccion VARCHAR(250),
    fecha_nacimiento DATE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL,
    sexo ENUM ('FEMENINO','MASCULINO','OTRO','PREFIERE_NO_INDICAR') NOT NULL,
    telefono VARCHAR(20),
    tipo_documento ENUM ('CARNET_EXTRANJERIA','DNI','PASAPORTE') NOT NULL,
    id_nacionalidad BIGINT,
    PRIMARY KEY (id_persona)
) ENGINE=InnoDB;

CREATE TABLE RegistroAuditoria (
    id_registro_auditoria BIGINT NOT NULL AUTO_INCREMENT,
    accion VARCHAR(80) NOT NULL,
    actor_identificador VARCHAR(100) NOT NULL,
    correlacion_id VARCHAR(64) NOT NULL,
    direccion_ip VARCHAR(64),
    entidad VARCHAR(80) NOT NULL,
    fecha_hora DATETIME(6) NOT NULL,
    modulo VARCHAR(80) NOT NULL,
    motivo VARCHAR(500),
    nuevo_valor TEXT,
    registro_id VARCHAR(80),
    resultado ENUM ('EXITOSO','FALLIDO','RECHAZADO') NOT NULL,
    valor_anterior TEXT,
    id_rol_actor BIGINT,
    PRIMARY KEY (id_registro_auditoria)
) ENGINE=InnoDB;

CREATE TABLE RegistroError (
    id_registro_error BIGINT NOT NULL AUTO_INCREMENT,
    componente VARCHAR(150) NOT NULL,
    correlacion_id VARCHAR(64) NOT NULL,
    detalle_tecnico_seguro TEXT,
    estado_error ENUM ('EN_SEGUIMIENTO','PENDIENTE','RESUELTO') NOT NULL,
    fecha_hora DATETIME(6) NOT NULL,
    mensaje_seguro VARCHAR(500) NOT NULL,
    observacion_seguimiento VARCHAR(1000),
    severidad ENUM ('ADVERTENCIA','CRITICA','ERROR','INFORMATIVA') NOT NULL,
    PRIMARY KEY (id_registro_error)
) ENGINE=InnoDB;

CREATE TABLE Rol (
    id_rol BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    descripcion VARCHAR(250),
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_rol)
) ENGINE=InnoDB;

CREATE TABLE RolPermiso (
    id_rol BIGINT NOT NULL,
    id_permiso BIGINT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE Seccion (
    id_seccion BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    anio_academico INTEGER NOT NULL,
    capacidad_maxima INTEGER NOT NULL,
    grado INTEGER NOT NULL,
    nombre VARCHAR(20) NOT NULL,
    id_docente_tutor BIGINT,
    PRIMARY KEY (id_seccion)
) ENGINE=InnoDB;

CREATE TABLE SesionAcceso (
    id_sesion_acceso BIGINT NOT NULL AUTO_INCREMENT,
    estado_sesion ENUM ('EXPIRADA','REVOCADA','VIGENTE') NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_expiracion DATETIME(6) NOT NULL,
    fecha_revocacion DATETIME(6),
    token_hash VARCHAR(64) NOT NULL,
    id_acceso_usuario BIGINT NOT NULL,
    PRIMARY KEY (id_sesion_acceso)
) ENGINE=InnoDB;

CREATE TABLE SolicitudMatricula (
    id_solicitud_matricula BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    estado_solicitud ENUM ('BORRADOR','CANCELADA','ENVIADA','EN_REVISION','MATRICULA_FINALIZADA','OBSERVADA','RECHAZADA') NOT NULL,
    fecha_envio DATETIME(6),
    fecha_revision DATETIME(6),
    motivo_rechazo VARCHAR(700),
    observaciones VARCHAR(1000),
    id_alumno BIGINT NOT NULL,
    id_seccion BIGINT NOT NULL,
    PRIMARY KEY (id_solicitud_matricula)
) ENGINE=InnoDB;

CREATE TABLE Tarea (
    id_tarea BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    descripcion VARCHAR(3000) NOT NULL,
    estado_publicacion ENUM ('ARCHIVADA','BORRADOR','CERRADA','PUBLICADA') NOT NULL,
    fecha_limite DATETIME(6) NOT NULL,
    fecha_publicacion DATETIME(6),
    max_archivos_entrega INTEGER NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    puntaje_maximo INTEGER NOT NULL,
    id_asignacion_curso BIGINT NOT NULL,
    PRIMARY KEY (id_tarea)
) ENGINE=InnoDB;

CREATE TABLE TareaArchivo (
    id_tarea BIGINT NOT NULL,
    id_archivo BIGINT NOT NULL,
    PRIMARY KEY (id_tarea, id_archivo)
) ENGINE=InnoDB;

CREATE TABLE TransaccionPago (
    id_transaccion_pago BIGINT NOT NULL AUTO_INCREMENT,
    activo BIT NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    estado_transaccion ENUM ('ANULADA','APROBADA','EXPIRADA','PENDIENTE','RECHAZADA') NOT NULL,
    fecha_transaccion DATETIME(6) NOT NULL,
    medio_pago ENUM ('EFECTIVO','PLIN','POS','TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA_BANCARIA','YAPE') NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    numero_operacion VARCHAR(100) NOT NULL,
    observacion VARCHAR(600),
    id_administrador BIGINT,
    id_apoderado BIGINT,
    id_compromiso_pago BIGINT NOT NULL,
    PRIMARY KEY (id_transaccion_pago)
) ENGINE=InnoDB;

-- =====================================================================
-- 2. RESTRICCIONES UNIQUE (41)
-- =====================================================================

ALTER TABLE AccesoUsuario 
   ADD CONSTRAINT uk_acceso_usuario_persona UNIQUE (id_persona);

ALTER TABLE AccesoUsuario 
   ADD CONSTRAINT uk_acceso_usuario_username UNIQUE (username);

ALTER TABLE AccesoUsuario 
   ADD CONSTRAINT uk_acceso_usuario_correo UNIQUE (correo);

ALTER TABLE Administrador 
   ADD CONSTRAINT uk_administrador_persona UNIQUE (id_persona);

ALTER TABLE Alumno 
   ADD CONSTRAINT uk_alumno_persona UNIQUE (id_persona);

ALTER TABLE Alumno 
   ADD CONSTRAINT uk_alumno_codigo UNIQUE (codigo_alumno);

ALTER TABLE AlumnoApoderado 
   ADD CONSTRAINT uk_alumno_apoderado UNIQUE (id_alumno, id_apoderado);

ALTER TABLE Apoderado 
   ADD CONSTRAINT uk_apoderado_persona UNIQUE (id_persona);

ALTER TABLE ArchivoAdjunto 
   ADD CONSTRAINT uk_archivo_nombre_almacenado UNIQUE (nombre_almacenado);

ALTER TABLE ArchivoAdjunto 
   ADD CONSTRAINT uk_archivo_ruta UNIQUE (ruta_archivo);

ALTER TABLE AsignacionCurso 
   ADD CONSTRAINT uk_asignacion_curso_seccion UNIQUE (id_curso, id_seccion);

ALTER TABLE AsistenciaAlumno 
   ADD CONSTRAINT uk_asistencia_alumno_fecha UNIQUE (id_alumno, id_asignacion_curso, fecha_registro);

ALTER TABLE AsistenciaDocente 
   ADD CONSTRAINT uk_asistencia_docente_jornada UNIQUE (id_docente, fecha_jornada);

ALTER TABLE AsistenciaDocente 
   ADD CONSTRAINT uk_asistencia_docente_qr UNIQUE (id_codigo_qr);

ALTER TABLE Aula 
   ADD CONSTRAINT uk_aula_codigo UNIQUE (codigo);

ALTER TABLE Calificacion 
   ADD CONSTRAINT uk_calificacion_evaluacion_alumno_competencia UNIQUE (id_evaluacion, id_alumno, id_competencia);

ALTER TABLE CategoriaPermiso 
   ADD CONSTRAINT uk_categoria_permiso_nombre UNIQUE (nombre);

ALTER TABLE CodigoQRAsistencia 
   ADD CONSTRAINT uk_codigo_qr_token_hash UNIQUE (token_hash);

ALTER TABLE Competencia 
   ADD CONSTRAINT uk_competencia_codigo UNIQUE (codigo);

ALTER TABLE Comprobante 
   ADD CONSTRAINT uk_comprobante_transaccion UNIQUE (id_transaccion_pago);

ALTER TABLE Comprobante 
   ADD CONSTRAINT uk_comprobante_serie_numero UNIQUE (serie, numero);

ALTER TABLE Comprobante 
   ADD CONSTRAINT UKaj0acp3dyp8xwji1a01yn788c UNIQUE (id_archivo_adjunto);

ALTER TABLE CompromisoPago 
   ADD CONSTRAINT uk_compromiso_pago_codigo UNIQUE (codigo_compromiso);

ALTER TABLE Curso 
   ADD CONSTRAINT uk_curso_codigo UNIQUE (codigo);

ALTER TABLE CursoCompetencia 
   ADD CONSTRAINT uk_curso_competencia UNIQUE (id_curso, id_competencia);

ALTER TABLE Docente 
   ADD CONSTRAINT uk_docente_persona UNIQUE (id_persona);

ALTER TABLE Docente 
   ADD CONSTRAINT uk_docente_codigo UNIQUE (codigo_docente);

ALTER TABLE EntregaTarea 
   ADD CONSTRAINT uk_entrega_tarea_alumno UNIQUE (id_tarea, id_alumno);

ALTER TABLE JustificacionAsistencia 
   ADD CONSTRAINT uk_justificacion_asistencia UNIQUE (id_asistencia_alumno);

ALTER TABLE Matricula 
   ADD CONSTRAINT uk_matricula_codigo UNIQUE (codigo_matricula);

ALTER TABLE Matricula 
   ADD CONSTRAINT uk_matricula_solicitud UNIQUE (id_solicitud_matricula);

ALTER TABLE Matricula 
   ADD CONSTRAINT uk_matricula_alumno_anio UNIQUE (id_alumno, anio_academico);

ALTER TABLE Nacionalidad 
   ADD CONSTRAINT uk_nacionalidad_nombre UNIQUE (nombre);

ALTER TABLE Permiso 
   ADD CONSTRAINT uk_permiso_codigo UNIQUE (codigo);

ALTER TABLE Persona 
   ADD CONSTRAINT uk_persona_tipo_numero_documento UNIQUE (tipo_documento, numero_documento);

ALTER TABLE Persona 
   ADD CONSTRAINT uk_persona_correo UNIQUE (correo);

ALTER TABLE Rol 
   ADD CONSTRAINT uk_rol_nombre UNIQUE (nombre);

ALTER TABLE RolPermiso 
   ADD CONSTRAINT uk_rol_permiso UNIQUE (id_rol, id_permiso);

ALTER TABLE Seccion 
   ADD CONSTRAINT uk_seccion_anio_grado_nombre UNIQUE (anio_academico, grado, nombre);

ALTER TABLE SesionAcceso 
   ADD CONSTRAINT uk_sesion_token_hash UNIQUE (token_hash);

ALTER TABLE TransaccionPago 
   ADD CONSTRAINT uk_transaccion_numero_operacion UNIQUE (numero_operacion);

-- =====================================================================
-- 3. CLAVES FORANEAS (60)
-- =====================================================================

ALTER TABLE AccesoUsuario 
   ADD CONSTRAINT FK6y4p4xu62s3o68ce3bo8r519o 
   FOREIGN KEY (id_persona) 
   REFERENCES Persona (id_persona);

ALTER TABLE AccesoUsuario 
   ADD CONSTRAINT FK4swherjbkoebn5tdkvfx1l8lt 
   FOREIGN KEY (id_rol) 
   REFERENCES Rol (id_rol);

ALTER TABLE Administrador 
   ADD CONSTRAINT FK6r6386kdagyc7mvq0nb6s553r 
   FOREIGN KEY (id_persona) 
   REFERENCES Persona (id_persona);

ALTER TABLE AlertaRiesgo 
   ADD CONSTRAINT FKk6afqwjafcgvk3kp1k9o4wvyh 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE AlertaRiesgo 
   ADD CONSTRAINT FK4hf7dn5yusm9nuo7puokpjlqr 
   FOREIGN KEY (id_criterio_riesgo) 
   REFERENCES CriterioRiesgo (id_criterio_riesgo);

ALTER TABLE Alumno 
   ADD CONSTRAINT FKntvhnoasw1yr2e6p6nudb5lu3 
   FOREIGN KEY (id_persona) 
   REFERENCES Persona (id_persona);

ALTER TABLE AlumnoApoderado 
   ADD CONSTRAINT FKh3uu7s7gjjfu94accxfffroe7 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE AlumnoApoderado 
   ADD CONSTRAINT FKbmcdymmt2ivbcc6m10ebp7c56 
   FOREIGN KEY (id_apoderado) 
   REFERENCES Apoderado (id_apoderado);

ALTER TABLE Apoderado 
   ADD CONSTRAINT FKb1vnr9jak635uoah7827s9upc 
   FOREIGN KEY (id_persona) 
   REFERENCES Persona (id_persona);

ALTER TABLE ArchivoAdjunto 
   ADD CONSTRAINT FKkjw8r9nepddp4h34rgsasrvs7 
   FOREIGN KEY (id_persona_carga) 
   REFERENCES Persona (id_persona);

ALTER TABLE AsignacionCurso 
   ADD CONSTRAINT FK597eh1vq5pxksgoo5h7prqv3s 
   FOREIGN KEY (id_curso) 
   REFERENCES Curso (id_curso);

ALTER TABLE AsignacionCurso 
   ADD CONSTRAINT FK5or40n4ffv0t22f77pldnx0p8 
   FOREIGN KEY (id_docente) 
   REFERENCES Docente (id_docente);

ALTER TABLE AsignacionCurso 
   ADD CONSTRAINT FK3h62ygdb8jx8fi201u7pq6ntg 
   FOREIGN KEY (id_seccion) 
   REFERENCES Seccion (id_seccion);

ALTER TABLE AsistenciaAlumno 
   ADD CONSTRAINT FK3vhu18npt6sdjn68b56mi0240 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE AsistenciaAlumno 
   ADD CONSTRAINT FKp5adh2raccdmd3rjogalkhrci 
   FOREIGN KEY (id_asignacion_curso) 
   REFERENCES AsignacionCurso (id_asignacion_curso);

ALTER TABLE AsistenciaAlumno 
   ADD CONSTRAINT FKm1i1cgno2id2hn2duye1ddtdr 
   FOREIGN KEY (id_docente_registrador) 
   REFERENCES Docente (id_docente);

ALTER TABLE AsistenciaDocente 
   ADD CONSTRAINT FK94e57g2vwis2gpi4qujcjxsgw 
   FOREIGN KEY (id_codigo_qr) 
   REFERENCES CodigoQRAsistencia (id_codigo_qr);

ALTER TABLE AsistenciaDocente 
   ADD CONSTRAINT FK76jin8t5en0sdqnxwdkjlwjhv 
   FOREIGN KEY (id_docente) 
   REFERENCES Docente (id_docente);

ALTER TABLE Calificacion 
   ADD CONSTRAINT FK4qry82d8sum2yp5vtlqcjpb7a 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE Calificacion 
   ADD CONSTRAINT FKckrrm6reiwvsoep5njo3pcbm6 
   FOREIGN KEY (id_competencia) 
   REFERENCES Competencia (id_competencia);

ALTER TABLE Calificacion 
   ADD CONSTRAINT FKabtl41u316qs7fmux53kh0xr3 
   FOREIGN KEY (id_evaluacion) 
   REFERENCES Evaluacion (id_evaluacion);

ALTER TABLE CodigoQRAsistencia 
   ADD CONSTRAINT FK389foxsfk6k8t9n23n514vtku 
   FOREIGN KEY (id_docente) 
   REFERENCES Docente (id_docente);

ALTER TABLE Comprobante 
   ADD CONSTRAINT FKst5lk0tg5wa4jap7851lyo5a2 
   FOREIGN KEY (id_archivo_adjunto) 
   REFERENCES ArchivoAdjunto (id_archivo);

ALTER TABLE Comprobante 
   ADD CONSTRAINT FKfgxa9ke7b5edy8ofmpqhmqwcc 
   FOREIGN KEY (id_transaccion_pago) 
   REFERENCES TransaccionPago (id_transaccion_pago);

ALTER TABLE CompromisoPago 
   ADD CONSTRAINT FKabp7e441ejyqeg809rb1png2d 
   FOREIGN KEY (id_matricula) 
   REFERENCES Matricula (id_matricula);

ALTER TABLE CursoCompetencia 
   ADD CONSTRAINT FKl48dd1rlpbtp8xva5s29jx8yl 
   FOREIGN KEY (id_competencia) 
   REFERENCES Competencia (id_competencia);

ALTER TABLE CursoCompetencia 
   ADD CONSTRAINT FK1xx8tdds2d3we7onu83g5s1ml 
   FOREIGN KEY (id_curso) 
   REFERENCES Curso (id_curso);

ALTER TABLE Docente 
   ADD CONSTRAINT FKivfm9r2g4r0jmxoepjykrhccr 
   FOREIGN KEY (id_persona) 
   REFERENCES Persona (id_persona);

ALTER TABLE EntregaTarea 
   ADD CONSTRAINT FKgurv5e4kg7kyhhynyqrgfam1j 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE EntregaTarea 
   ADD CONSTRAINT FKn8p0sv5gwb5n11ltxtulfc6n2 
   FOREIGN KEY (id_tarea) 
   REFERENCES Tarea (id_tarea);

ALTER TABLE EntregaTareaArchivo 
   ADD CONSTRAINT FKsjkdxc58kvdfa8g80fs5hkj99 
   FOREIGN KEY (id_archivo) 
   REFERENCES ArchivoAdjunto (id_archivo);

ALTER TABLE EntregaTareaArchivo 
   ADD CONSTRAINT FKlyrprhmemiwt7im0e523hfrgc 
   FOREIGN KEY (id_entrega_tarea) 
   REFERENCES EntregaTarea (id_entrega_tarea);

ALTER TABLE Evaluacion 
   ADD CONSTRAINT FKmqky1es4arreunalu3i4h0et9 
   FOREIGN KEY (id_asignacion_curso) 
   REFERENCES AsignacionCurso (id_asignacion_curso);

ALTER TABLE EvaluacionCompetencia 
   ADD CONSTRAINT FKaxigwm3k5nce5xj5nxafd4t1m 
   FOREIGN KEY (id_competencia) 
   REFERENCES Competencia (id_competencia);

ALTER TABLE EvaluacionCompetencia 
   ADD CONSTRAINT FKfouakfhqiingh88vav8bbv724 
   FOREIGN KEY (id_evaluacion) 
   REFERENCES Evaluacion (id_evaluacion);

ALTER TABLE Horario 
   ADD CONSTRAINT FKn9a5gk5ceva2440isx1oknv5w 
   FOREIGN KEY (id_asignacion_curso) 
   REFERENCES AsignacionCurso (id_asignacion_curso);

ALTER TABLE Horario 
   ADD CONSTRAINT FK74io69d6km297uuf1s9mt9bej 
   FOREIGN KEY (id_aula) 
   REFERENCES Aula (id_aula);

ALTER TABLE JustificacionAsistencia 
   ADD CONSTRAINT FKdqgj41vyb3do98frmpt3e0b1t 
   FOREIGN KEY (id_asistencia_alumno) 
   REFERENCES AsistenciaAlumno (id_asistencia_alumno);

ALTER TABLE JustificacionAsistencia 
   ADD CONSTRAINT FKc79m1g5n2gy7g9bqv7osuctt4 
   FOREIGN KEY (id_docente_revisor) 
   REFERENCES Docente (id_docente);

ALTER TABLE JustificacionAsistenciaArchivo 
   ADD CONSTRAINT FKl4p9avev4g3x99lirv5wnt3a3 
   FOREIGN KEY (id_archivo) 
   REFERENCES ArchivoAdjunto (id_archivo);

ALTER TABLE JustificacionAsistenciaArchivo 
   ADD CONSTRAINT FKf0ympdaamlnlh0r14psqjjr0h 
   FOREIGN KEY (id_justificacion_asistencia) 
   REFERENCES JustificacionAsistencia (id_justificacion_asistencia);

ALTER TABLE Matricula 
   ADD CONSTRAINT FK7hs7wugi5i1l9wqnu9fkm7oav 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE Matricula 
   ADD CONSTRAINT FK1ygpptwi451vsi05f5jao77kf 
   FOREIGN KEY (id_seccion) 
   REFERENCES Seccion (id_seccion);

ALTER TABLE Matricula 
   ADD CONSTRAINT FKit3u84yluc10rxfbueng5643h 
   FOREIGN KEY (id_solicitud_matricula) 
   REFERENCES SolicitudMatricula (id_solicitud_matricula);

ALTER TABLE Notificacion 
   ADD CONSTRAINT FKoio90wks6txy9tx7kgjxqntqt 
   FOREIGN KEY (id_acceso_usuario) 
   REFERENCES AccesoUsuario (id_acceso_usuario);

ALTER TABLE Permiso 
   ADD CONSTRAINT FKa4ah8fe8q4khc1qcpvfjb0jg4 
   FOREIGN KEY (id_categoria_permiso) 
   REFERENCES CategoriaPermiso (id_categoria_permiso);

ALTER TABLE Persona 
   ADD CONSTRAINT FK7kfs2v5mcesajmhbhg3yxpy4h 
   FOREIGN KEY (id_nacionalidad) 
   REFERENCES Nacionalidad (id_nacionalidad);

ALTER TABLE RegistroAuditoria 
   ADD CONSTRAINT FKm45w6258ql9a2jt5tv1w79y6v 
   FOREIGN KEY (id_rol_actor) 
   REFERENCES Rol (id_rol);

ALTER TABLE RolPermiso 
   ADD CONSTRAINT FKrhxhgw05bdvokfrpppumlfh5d 
   FOREIGN KEY (id_permiso) 
   REFERENCES Permiso (id_permiso);

ALTER TABLE RolPermiso 
   ADD CONSTRAINT FKsxc3d8lmtj7em6n8j0wl4jwco 
   FOREIGN KEY (id_rol) 
   REFERENCES Rol (id_rol);

ALTER TABLE Seccion 
   ADD CONSTRAINT FKvhni8jgqlcxttqalae12ggqs 
   FOREIGN KEY (id_docente_tutor) 
   REFERENCES Docente (id_docente);

ALTER TABLE SesionAcceso 
   ADD CONSTRAINT FK3f83rgw3jcn33yxeb8vhyhi8d 
   FOREIGN KEY (id_acceso_usuario) 
   REFERENCES AccesoUsuario (id_acceso_usuario);

ALTER TABLE SolicitudMatricula 
   ADD CONSTRAINT FKa28q0d90egxeh2b0817x1pprr 
   FOREIGN KEY (id_alumno) 
   REFERENCES Alumno (id_alumno);

ALTER TABLE SolicitudMatricula 
   ADD CONSTRAINT FKtchlanx1dooy7r74iud2ie0cp 
   FOREIGN KEY (id_seccion) 
   REFERENCES Seccion (id_seccion);

ALTER TABLE Tarea 
   ADD CONSTRAINT FKgjykyimt8nfj0aao1t3vnc24e 
   FOREIGN KEY (id_asignacion_curso) 
   REFERENCES AsignacionCurso (id_asignacion_curso);

ALTER TABLE TareaArchivo 
   ADD CONSTRAINT FKmhknmq9287nu4rvxc79kx1jpf 
   FOREIGN KEY (id_archivo) 
   REFERENCES ArchivoAdjunto (id_archivo);

ALTER TABLE TareaArchivo 
   ADD CONSTRAINT FK6uk44vey1lsksq288pgl3kkyy 
   FOREIGN KEY (id_tarea) 
   REFERENCES Tarea (id_tarea);

ALTER TABLE TransaccionPago 
   ADD CONSTRAINT FK9pa6ogc7y5nx7i7frq1qp4aja 
   FOREIGN KEY (id_administrador) 
   REFERENCES Administrador (id_administrador);

ALTER TABLE TransaccionPago 
   ADD CONSTRAINT FKhe62k6wxbmbcmqgph63rrt8mr 
   FOREIGN KEY (id_apoderado) 
   REFERENCES Apoderado (id_apoderado);

ALTER TABLE TransaccionPago 
   ADD CONSTRAINT FKaask5gotx5cvxvnw29ker2p47 
   FOREIGN KEY (id_compromiso_pago) 
   REFERENCES CompromisoPago (id_compromiso_pago);

-- FIN DEL SCRIPT
