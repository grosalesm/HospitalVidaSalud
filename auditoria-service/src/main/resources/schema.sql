CREATE TABLE IF NOT EXISTS registros_auditoria (
    id              BIGSERIAL    PRIMARY KEY,
    tipo_evento     VARCHAR(50)  NOT NULL,
    tipo_recurso    VARCHAR(50)  NOT NULL,
    recurso_id      BIGINT       NOT NULL,
    paciente_id     BIGINT       NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo    VARCHAR(30),
    origen          VARCHAR(100),
    descripcion     TEXT,
    fecha_evento    TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aud_recurso  ON registros_auditoria (tipo_recurso, recurso_id);
CREATE INDEX IF NOT EXISTS idx_aud_paciente ON registros_auditoria (paciente_id);
CREATE INDEX IF NOT EXISTS idx_aud_evento   ON registros_auditoria (tipo_evento);