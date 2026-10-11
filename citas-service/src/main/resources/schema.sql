-- Base de datos: citas_db
-- Ejecutar manualmente antes de iniciar el servicio

CREATE TABLE IF NOT EXISTS citas (
    id             BIGSERIAL    PRIMARY KEY,
    paciente_id    BIGINT       NOT NULL,
    especialidad   VARCHAR(100) NOT NULL,
    fecha_hora     TIMESTAMP    NOT NULL,
    estado         VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cita_paciente  ON citas (paciente_id);
CREATE INDEX IF NOT EXISTS idx_cita_estado    ON citas (estado);
CREATE INDEX IF NOT EXISTS idx_cita_pac_fecha ON citas (paciente_id, fecha_hora);