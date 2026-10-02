-- MySQL: ejecutar con la base de AsisControl seleccionada y copia de respaldo.
-- Detecta los nombres físicos tanto si conservan PascalCase como si usan snake_case.
-- Probado el modelo en H2; este script necesita verificación en una copia de MySQL.
SET @tabla = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND LOWER(REPLACE(TABLE_NAME, '_', '')) = 'accesousuario' LIMIT 1);
-- Si @tabla es NULL, detenerse y revisar el esquema antes de continuar.
SET @existe_compuesta = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tabla AND INDEX_NAME = 'uk_acceso_usuario_persona_rol');
SET @sql = IF(@existe_compuesta > 0, 'SELECT 1', CONCAT('ALTER TABLE `', @tabla,
    '` ADD CONSTRAINT uk_acceso_usuario_persona_rol UNIQUE (id_persona, id_rol)'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
-- La clave compuesta también mantiene el índice requerido por la FK de persona.
SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', indices.INDEX_NAME, '`') SEPARATOR ', ')
INTO @quitar FROM (
    SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tabla AND NON_UNIQUE = 0 AND INDEX_NAME <> 'PRIMARY'
    GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'id_persona'
) AS indices;
SET @sql = IF(@quitar IS NULL, 'SELECT 1', CONCAT('ALTER TABLE `', @tabla, '` ', @quitar));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
-- Si parentesco es ENUM, permitir la ausencia de un parentesco declarado.
SET @vinculo = (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND LOWER(REPLACE(TABLE_NAME, '_', '')) = 'alumnoapoderado' LIMIT 1);
SET @es_enum = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = @vinculo AND COLUMN_NAME = 'parentesco' AND DATA_TYPE = 'enum');
SET @sql = IF(@es_enum = 0, 'SELECT 1', CONCAT('ALTER TABLE `', @vinculo,
    '` MODIFY parentesco ENUM(''PADRE'',''MADRE'',''TUTOR_LEGAL'',''OTRO_FAMILIAR'',''NO_DECLARADO'') NOT NULL'));
PREPARE migracion FROM @sql; EXECUTE migracion; DEALLOCATE PREPARE migracion;
