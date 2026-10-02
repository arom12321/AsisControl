-- AsisControl · 003 · Seguridad, recuperación y solicitud del apoderado · 2026-10-02.
-- Aplicar una vez, después de 001 y 002, con respaldo de la base MySQL 8.
-- La aplicación usa ddl-auto=validate: esta migración NO se aplica automáticamente.
-- Conservar las fichas, cuentas, matrículas y auditoría. Revocar las sesiones del esquema anterior.
-- Las cuentas existentes quedan con correo NO confirmado: el administrador debe comprobarlo.
-- Las temporales antiguas vencen conservadoramente 24 h después de la creación del acceso.
-- Si la temporal ya venció, usar el restablecimiento manual verificando identidad y motivo.
-- Este SQL se entrega para revisión; no se ejecutó en una instancia MySQL en esta sesión.

SET @usuarios=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='accesousuario' LIMIT 1);
SET @sesiones=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='sesionacceso' LIMIT 1);
SET @solicitudes=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='solicitudmatricula' LIMIT 1);
SET @secciones=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='seccion' LIMIT 1);
SET @anios=(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=DATABASE() AND LOWER(REPLACE(TABLE_NAME,'_',''))='anioacademico' LIMIT 1);
-- Si alguno de los nombres anteriores es NULL, detenerse y seleccionar la base correcta.

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@usuarios AND COLUMN_NAME='correo_verificado');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@usuarios,'` ADD COLUMN correo_verificado BIT NOT NULL DEFAULT 0'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@usuarios AND COLUMN_NAME='contrasena_temporal_expira');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@usuarios,'` ADD COLUMN contrasena_temporal_expira DATETIME(6) NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('UPDATE `',@usuarios,'` SET contrasena_temporal_expira=DATE_ADD(fecha_creacion,INTERVAL 24 HOUR) WHERE requiere_cambio_contrasena=1 AND contrasena_temporal_expira IS NULL');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@sesiones AND COLUMN_NAME='ultima_actividad');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@sesiones,'` ADD COLUMN ultima_actividad DATETIME(6) NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('UPDATE `',@sesiones,'` SET ultima_actividad=COALESCE(ultima_actividad,fecha_creacion),estado_sesion=''REVOCADA'',fecha_revocacion=UTC_TIMESTAMP(6) WHERE estado_sesion=''VIGENTE''');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('UPDATE `',@sesiones,'` SET ultima_actividad=fecha_creacion WHERE ultima_actividad IS NULL');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('ALTER TABLE `',@sesiones,'` MODIFY ultima_actividad DATETIME(6) NOT NULL');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@anios AND COLUMN_NAME='admision_abierta');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@anios,'` ADD COLUMN admision_abierta BIT NOT NULL DEFAULT 0'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@solicitudes AND COLUMN_NAME='anio_academico');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@solicitudes,'` ADD COLUMN anio_academico INT NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @existe=(SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=@solicitudes AND COLUMN_NAME='grado');
SET @sql=IF(@existe>0,'SELECT 1',CONCAT('ALTER TABLE `',@solicitudes,'` ADD COLUMN grado INT NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('UPDATE `',@solicitudes,'` so JOIN `',@secciones,'` se ON so.id_seccion=se.id_seccion SET so.anio_academico=se.anio_academico,so.grado=se.grado WHERE so.anio_academico IS NULL OR so.grado IS NULL');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('ALTER TABLE `',@solicitudes,'` MODIFY anio_academico INT NOT NULL, MODIFY grado INT NOT NULL, MODIFY id_seccion BIGINT NULL');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

SET @sql=CONCAT('CREATE TABLE IF NOT EXISTS token_recuperacion (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,id_acceso_usuario BIGINT NOT NULL,token_hash VARCHAR(64) NOT NULL,creado DATETIME(6) NOT NULL,expira DATETIME(6) NOT NULL,usado DATETIME(6) NULL,revocado DATETIME(6) NULL,CONSTRAINT uk_recuperacion_hash UNIQUE(token_hash),CONSTRAINT fk_recuperacion_usuario FOREIGN KEY(id_acceso_usuario) REFERENCES `',@usuarios,'`(id_acceso_usuario)) ENGINE=InnoDB');
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;

CREATE TABLE IF NOT EXISTS limite_recuperacion (clave VARCHAR(64) NOT NULL PRIMARY KEY,inicio DATETIME(6) NOT NULL,cantidad INT NOT NULL) ENGINE=InnoDB;

-- El arranque del backend añade SOLICITUDES_ESCRIBIR a ADMINISTRADOR y APODERADO.
-- No se asigna MATRÍCULA_ESCRIBIR al apoderado. La admisión permanece cerrada hasta abrirla en la aplicación.
