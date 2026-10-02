# AsisControl Backend

Backend del **Sistema de Información Web para la Gestión Integral de un Colegio de Educación Secundaria**. El proyecto implementa el modelo de clases de Lucidchart, los flujos de los diagramas de estado y los contratos requeridos por las pantallas de Figma.

## Tecnología

- Java 21 LTS.
- Spring Boot 4.1.1.
- Spring Web, Validation, Data JPA y Security.
- JWT firmado con HMAC-SHA256 y sesiones revocables almacenadas como hash.
- MySQL para el entorno real.
- H2 en modo de compatibilidad MySQL para desarrollo y pruebas.
- OpenAPI/Swagger.
- Maven.

El backend es un proyecto Maven multimódulo. Las capas principales son módulos independientes y el
`pom.xml` de la raíz actúa como agregador:

```text
backend/
├── pom.xml                 Agregador Maven
├── entity/                 Entidades JPA y enumeraciones
├── dto/                    Contratos de entrada y salida
├── repository/             Persistencia con Spring Data JPA
├── service/                Reglas de negocio y transacciones
│   └── src/main/java/com/asiscontrol/{service,security,exception,util}
└── controller/             API REST y aplicación ejecutable
    └── src/main/java/com/asiscontrol/{controller,config}
```

Las dependencias siguen una sola dirección: `controller` consume `service`; `service` consume
`repository`, `dto` y `entity`; `repository` consume `entity`; y `dto` consume los tipos del modelo
que necesita para sus contratos. Esto evita dependencias circulares entre capas.

## Abrir en IntelliJ IDEA

1. Abra IntelliJ IDEA.
2. Seleccione **Open**.
3. Elija la carpeta donde está el proyecto o directamente su `pom.xml`.
4. Seleccione un JDK 21 en **Project SDK**.
5. Espere a que Maven termine de descargar e indexar las dependencias.
6. Si IntelliJ lo solicita, habilite **annotation processing** para Lombok.
7. Ejecute `AsisControlApplication`, ubicado en el módulo `controller`.

No es necesario instalar MySQL para el primer arranque: el perfil predeterminado `local` usa una base H2 en memoria.

## Ejecución local

```powershell
.\mvnw.cmd -DskipTests install
.\mvnw.cmd -f controller\pom.xml spring-boot:run
```

Para disponer de una cuenta administrativa local, defina antes estas variables; no se incluye ninguna contraseña fija en el código:

```powershell
$env:BOOTSTRAP_ADMIN_DOCUMENT = "00000000"
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@colegio.edu.pe"
$env:BOOTSTRAP_ADMIN_PASSWORD = "una-clave-segura-de-prueba"
$env:BOOTSTRAP_ADMIN_USERNAME = "admin"
.\mvnw.cmd -DskipTests install
.\mvnw.cmd -f controller\pom.xml spring-boot:run
```

Al ingresar por primera vez, la API obliga a cambiar la contraseña temporal antes de permitir el
acceso al resto de módulos. Solo quedan disponibles `/api/auth/me`, `/api/auth/change-password` y
`/api/auth/logout` hasta completar el cambio.

Direcciones útiles:

- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Salud: `http://localhost:8080/actuator/health`
- Consola H2 local: `http://localhost:8080/h2-console`

## Ejecución con MySQL

Copie los nombres de variables de `.env.example` a la configuración de ejecución de IntelliJ o al entorno del sistema. Las variables mínimas son:

```text
SPRING_PROFILES_ACTIVE=mysql
DB_URL=jdbc:mysql://localhost:3306/asiscontrol?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
DB_USERNAME=asiscontrol_app
DB_PASSWORD=...
JWT_SECRET=... mínimo 32 caracteres ...
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

El perfil `mysql` usa Flyway para versionar el esquema y conserva `ddl-auto=validate` para que Hibernate compruebe que las entidades coinciden con las tablas sin modificarlas por su cuenta.

La migración `V1__baseline_schema.sql` contiene el esquema completo. Si la base está vacía, Flyway crea las 43 tablas. Si la base ya contiene ese esquema pero todavía no tiene historial de Flyway, `baseline-on-migrate` registra la versión 1 sin intentar volver a crear las tablas.

Para cada cambio posterior de estructura, agregue un archivo nuevo en `controller/src/main/resources/db/migration`, por ejemplo:

```text
V2__agregar_columna_telefono_emergencia.sql
V3__crear_indice_busqueda_alumno.sql
```

No edite una migración que ya haya sido aplicada. Flyway valida sus checksums y ejecuta las pendientes en orden. Las credenciales reales deben permanecer únicamente en variables de entorno y nunca en Git.

## Módulos de la API

- `/api/auth`: inicio/cierre de sesión, usuario actual y cambio de contraseña.
- `/api/usuarios`, `/api/roles`: cuentas, roles y permisos.
- `/api/alumnos`, `/api/docentes`, `/api/apoderados`: personas, perfiles y vínculos familiares.
- `/api/catalogos`: nacionalidades y enumeraciones para combos del frontend.
- `/api/cursos`, `/api/competencias`, `/api/secciones`, `/api/aulas`, `/api/asignaciones-cursos`, `/api/horarios`: estructura académica.
- `/api/solicitudes-matricula`, `/api/matriculas`: solicitud, revisión, subsanación y matrícula.
- `/api/asistencias/alumnos`, `/api/asistencias/docentes`, `/api/justificaciones-asistencia`: asistencia, QR y justificaciones.
- `/api/tareas`, `/api/entregas-tarea`: asignaciones, archivos, entregas y calificación.
- `/api/evaluaciones`, `/api/calificaciones`: notas AD/A/B/C y consolidado por competencia.
- `/api/compromisos-pago`, `/api/transacciones-pago`, `/api/comprobantes`, `/api/simulaciones-pago`: obligaciones y pagos.
- `/api/criterios-riesgo`, `/api/alertas-riesgo`: detección y seguimiento de riesgo.
- `/api/evaluaciones-riesgo`: ejecución controlada de las reglas de detección.
- `/api/archivos`: carga y descarga autorizada de adjuntos.
- `/api/auditoria`: trazabilidad y seguimiento de errores.
- `/api/dashboard`: indicadores consumidos por los paneles de Figma.

Todos los listados grandes admiten paginación y filtros. Las entidades JPA nunca se exponen directamente: cada endpoint utiliza DTO de solicitud y respuesta.

## Formato uniforme de errores

```json
{
  "timestamp": "2026-09-29T12:00:00Z",
  "status": 409,
  "code": "VACANTE_NO_DISPONIBLE",
  "message": "No existen vacantes disponibles para la sección",
  "path": "/api/solicitudes-matricula/15/estado",
  "correlationId": "f8e44bb7-283f-449f-b836-636973cf4124",
  "fieldErrors": []
}
```

La API nunca devuelve stack traces, nombres de excepciones internas ni mensajes SQL.

## Verificación

```powershell
.\mvnw.cmd clean verify
```

Este comando compila todos los módulos, ejecuta las pruebas y genera el reporte agregado de JaCoCo
en `controller/target/site/jacoco-aggregate/index.html`.

## Seguridad

- Las contraseñas se almacenan con BCrypt.
- Un usuario se bloquea temporalmente tras cinco intentos fallidos.
- Los JWT tienen duración corta y su sesión puede revocarse.
- En base de datos solo se conserva el hash SHA-256 del token.
- Los roles y permisos son aplicados tanto en controladores como en reglas de propiedad.
- Los listados de alumnos, matrículas, asistencias, tareas, evaluaciones, entregas, pagos y alertas
  se restringen automáticamente al docente, alumno o apoderado autenticado cuando corresponde.
- CORS, secretos, conexión y almacenamiento se configuran mediante variables de entorno.
- Los archivos usan nombres UUID, lista blanca MIME, límite de tamaño, propietario y descarga controlada.
- El borrado funcional es lógico para preservar el historial.

## Flujo Git que se usará al integrar

Esta carpeta todavía no modifica el repositorio compartido. Cuando el equipo autorice la integración:

1. actualizar `develop`;
2. crear una rama `feature/backend-inicial`;
3. copiar el proyecto dentro de `backend/`;
4. ejecutar `mvn clean verify`;
5. crear commits con el formato `feat: ...`, `test: ...`, `docs: ...`;
6. abrir un Pull Request hacia `develop`.

No se debe trabajar directamente sobre `main` ni `develop`.
