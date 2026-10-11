-- Base de datos: pacientes_db
-- Ejecutar manualmente antes de iniciar el servicio

CREATE TABLE IF NOT EXISTS pacientes (
    id                BIGSERIAL    PRIMARY KEY,
    tipo_documento    VARCHAR(20)  NOT NULL,
    numero_documento  VARCHAR(20)  NOT NULL,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    telefono          VARCHAR(9),
    correo            VARCHAR(150),
    direccion         VARCHAR(200),
    fecha_registro    TIMESTAMP    NOT NULL DEFAULT NOW(),
    estado            VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT uq_documento UNIQUE (tipo_documento, numero_documento)
);

CREATE INDEX IF NOT EXISTS idx_pac_documento ON pacientes (tipo_documento, numero_documento);
CREATE INDEX IF NOT EXISTS idx_pac_estado    ON pacientes (estado);