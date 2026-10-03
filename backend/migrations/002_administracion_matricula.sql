-- MySQL 8: ejecutar en una copia de la base, después de 001_accesos_por_rol.sql.
-- Migración aditiva: conserva fichas, accesos, solicitudes, matrículas y errores anteriores.
-- No activa años ni inventa fechas: configure el año y grados desde la aplicación.
-- Validación automatizada del modelo: H2 en modo MySQL; este SQL no se ha ejecutado en MySQL.
CREATE TABLE IF NOT EXISTS anio_academico (
    id_anio_academico BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    anio INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('BORRADOR','ACTIVO','CERRADO') NOT NULL,
    clave_activa INT NULL,
    activo BIT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT uk_anio_numero UNIQUE(anio),
    CONSTRAINT uk_anio_activo UNIQUE(clave_activa)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS periodo_academico (
    id_periodo_academico BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_anio BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    orden INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('PLANIFICADO','ACTIVO','CERRADO') NOT NULL,
    clave_activa INT NULL,
    activo BIT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_periodo_anio FOREIGN KEY(id_anio) REFERENCES anio_academico(id_anio_academico),
    CONSTRAINT uk_periodo_orden UNIQUE(id_anio,orden),
    CONSTRAINT uk_periodo_activo UNIQUE(clave_activa)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS grado_academico (
    id_grado_academico BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_anio BIGINT NOT NULL,
    numero INT NOT NULL,
    capacidad_default INT NOT NULL,
    activo BIT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_actualizacion DATETIME(6) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_grado_anio FOREIGN KEY(id_anio) REFERENCES anio_academico(id_anio_academico),
    CONSTRAINT uk_grado_anio UNIQUE(id_anio,numero)
) ENGINE=InnoDB;
SET @errores = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='registroerror' LIMIT 1);
-- Si @errores o @matriculas es NULL, detenerse y revisar la base seleccionada.
SET @existe_version = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@errores AND COLUMN_NAME='version');
SET @sql = IF(@existe_version>0,'SELECT 1',CONCAT('ALTER TABLE `',@errores,'` ADD COLUMN version BIGINT NOT NULL DEFAULT 0'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS seguimiento_error (',
    'id_seguimiento_error BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, ',
    'id_registro_error BIGINT NOT NULL, actor VARCHAR(150) NOT NULL, fecha DATETIME(6) NOT NULL, ',
    'anterior ENUM(''PENDIENTE'',''EN_SEGUIMIENTO'',''RESUELTO'') NOT NULL, ',
    'nuevo ENUM(''PENDIENTE'',''EN_SEGUIMIENTO'',''RESUELTO'') NOT NULL, observacion VARCHAR(1000) NOT NULL, ',
    'CONSTRAINT fk_seguimiento_error FOREIGN KEY(id_registro_error) REFERENCES `',@errores,'`(id_registro_error)) ENGINE=InnoDB');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
SET @matriculas=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='matricula' LIMIT 1);
SET @existe_clave=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@matriculas AND COLUMN_NAME='clave_vigente');
SET @sql=IF(@existe_clave>0,'SELECT 1',CONCAT('ALTER TABLE `',@matriculas,'` ADD COLUMN clave_vigente INT NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
SET @sql=CONCAT('UPDATE `',@matriculas,'` SET clave_vigente=CASE WHEN activo=1 AND estado_matricula=''ACTIVA'' THEN 1 ELSE NULL END');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
SET @existe_indice=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@matriculas AND INDEX_NAME='uk_matricula_vigente');
SET @sql=IF(@existe_indice>0,'SELECT 1',CONCAT('ALTER TABLE `',@matriculas,'` ADD CONSTRAINT uk_matricula_vigente UNIQUE(id_alumno,anio_academico,clave_vigente)'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
-- La nueva clave conserva la unicidad de matrícula ACTIVA y permite otra tras anulación/retiro.
SELECT GROUP_CONCAT(CONCAT('DROP INDEX `',indices.INDEX_NAME,'`') SEPARATOR ', ') INTO @quitar
FROM (SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@matriculas AND NON_UNIQUE=0 AND INDEX_NAME<>'PRIMARY'
    GROUP BY INDEX_NAME HAVING COUNT(*)=2 AND SUM(COLUMN_NAME IN ('id_alumno','anio_academico'))=2) indices;
SET @sql=IF(@quitar IS NULL,'SELECT 1',CONCAT('ALTER TABLE `',@matriculas,'` ',@quitar));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
