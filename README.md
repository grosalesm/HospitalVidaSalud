# Hospital Vida Salud · Microservicios

Solución académica para la Segunda Evaluación T2 del curso Desarrollo de Aplicaciones Web II (Cibertec). Reemplaza el sistema monolítico del hospital por microservicios con Spring Boot, Spring Cloud Gateway, Eureka, RabbitMQ, Spring WebFlux y R2DBC sobre PostgreSQL.

> Todos los datos usados son ficticios. El sistema no debe usarse con información clínica real.

## Arquitectura

| Componente | Puerto | Responsabilidad | Base de datos |
|---|---|---|---|
| eureka-server | 8761 | Registro y descubrimiento de servicios | — |
| api-gateway | 8080 | Punto de entrada único; enruta `/api/**` por nombre (`lb://`) | — |
| pacientes-service | 8081 | Registro, consulta y validación de pacientes | pacientes_db |
| citas-service | 8082 | Programación de citas y control de estados; publica `CitaConfirmada` | citas_db |
| notificaciones-service | 8083 | Consume `CitaConfirmada` y genera el aviso | notificaciones_db |
| auditoria-service | 8084 | Consume `CitaConfirmada` y registra la trazabilidad | auditoria_db |

- **Comunicación síncrona:** Citas consulta a Pacientes con WebClient (timeout de 5 s) antes de crear una cita.
- **Comunicación asíncrona:** al confirmar una cita, Citas publica en el exchange `hospital.eventos` (topic) con routing key `cita.confirmada`. Las colas `notificaciones.cita-confirmada` y `auditoria.cita-confirmada` reciben el evento.
- **Persistencia:** cada servicio es dueño de su base PostgreSQL y accede a ella de forma reactiva con R2DBC. No hay claves foráneas entre bases.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL (puerto 5432)
- RabbitMQ (puerto 5672, panel en 15672, usuario `guest`)

## Configuración

1. Crear las cuatro bases de datos (las tablas se crean solas al iniciar cada servicio con `schema.sql`):

   ```sql
   CREATE DATABASE pacientes_db;
   CREATE DATABASE citas_db;
   CREATE DATABASE notificaciones_db;
   CREATE DATABASE auditoria_db;
   ```

2. Definir las credenciales como variables de entorno. La contraseña nunca se guarda en el repositorio.

   | Variable | Obligatoria | Valor por defecto |
      |---|---|---|
   | `DB_PASSWORD` | Sí | — |
   | `DB_USERNAME` | No | `postgres` |

   También se pueden configurar con `setup-env.ps1` (Windows) o `setup-env.sh` (Linux/Mac).

## Ejecución

Iniciar los servicios en este orden (desde el IDE o con `mvn spring-boot:run` dentro de cada carpeta):

1. eureka-server
2. pacientes-service
3. citas-service
4. notificaciones-service
5. auditoria-service
6. api-gateway

Verificar en `http://localhost:8761` que los cinco servicios aparecen registrados. Todas las solicitudes se hacen a través del Gateway en `http://localhost:8080`.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/pacientes` | Registrar paciente (409 si el documento ya existe) |
| GET | `/api/pacientes/{id}` | Consultar paciente |
| PUT | `/api/pacientes/{id}` | Actualizar paciente |
| PATCH | `/api/pacientes/{id}/estado?estado=ACTIVO` | Cambiar estado (ACTIVO / INACTIVO) |
| POST | `/api/citas` | Programar cita (valida al paciente) |
| PATCH | `/api/citas/{id}/confirmar` | Confirmar cita y publicar el evento |
| PATCH | `/api/citas/{id}/atender` | Registrar la atención simulada |
| PUT | `/api/citas/{id}/reprogramar` | Reprogramar cita |
| PATCH | `/api/citas/{id}/cancelar` | Cancelar cita |
| PATCH | `/api/citas/{id}/ausente` | Marcar ausencia |
| GET | `/api/notificaciones/paciente/{id}` | Notificaciones de un paciente |
| GET | `/api/auditoria/recurso/CITA/{id}` | Historial de cambios de una cita |

**Estados de la cita:** PENDIENTE → CONFIRMADA → ATENDIDA, además de CANCELADA y AUSENTE. Una transición no permitida responde 409 Conflict.

## Pruebas

**Pruebas automatizadas** (unitarias y de controlador, no requieren base de datos ni RabbitMQ):

```bash
mvn test
```

**Pruebas funcionales con Postman:** importar `postman/HospitalVidaSalud.postman_collection.json` y ejecutarla completa con el Collection Runner. Recorre el flujo de extremo a extremo (23 solicitudes y 39 validaciones) a través del Gateway, incluidos los casos de error.

## Integración continua

GitHub Actions compila los seis módulos y ejecuta las pruebas en cada push y pull request hacia `main` (`.github/workflows/ci.yml`). Si la compilación o una prueba falla, el pipeline se marca en rojo.

## Estructura del repositorio

```
├── eureka-server/
├── api-gateway/
├── pacientes-service/
├── citas-service/
├── notificaciones-service/
├── auditoria-service/
├── postman/
├── setup-env.ps1 / setup-env.sh
└── pom.xml            (proyecto agregador)
```