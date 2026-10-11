CREATE TABLE IF NOT EXISTS notificaciones (
    id             BIGSERIAL   PRIMARY KEY,
    tipo_evento    VARCHAR(50) NOT NULL,
    paciente_id    BIGINT      NOT NULL,
    referencia_id  BIGINT,
    mensaje        TEXT        NOT NULL,
    estado         VARCHAR(20) NOT NULL DEFAULT 'ENVIADA',
    fecha_creacion TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_notif_paciente    ON notificaciones (paciente_id);
CREATE INDEX IF NOT EXISTS idx_notif_tipo_evento ON notificaciones (tipo_evento);