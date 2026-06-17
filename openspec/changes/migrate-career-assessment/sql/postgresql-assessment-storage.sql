-- CyanCruise career assessment PostgreSQL storage.
-- Idempotent: safe to run more than once.
-- Adjust schema name if cc001.storage.postgresql.schema is not public.

CREATE TABLE IF NOT EXISTS public.cc_assessment_record (
    record_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    scale_id BIGINT,
    scale_title VARCHAR(255),
    status VARCHAR(32),
    result_summary VARCHAR(128),
    result_json JSONB,
    answers_json JSONB,
    suggested_roles_json JSONB,
    payload_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_assessment_record_user_time
    ON public.cc_assessment_record (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cc_assessment_record_scale
    ON public.cc_assessment_record (scale_id);
