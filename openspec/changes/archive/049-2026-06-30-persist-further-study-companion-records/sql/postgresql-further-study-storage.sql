-- CyanCruise further-study companion storage.
-- Target schema is configured by cc001.storage.postgresql.schema; adjust schema prefix if not public.
-- This script is for manual review and does not include passwords, grants, destructive DDL, or local paths.

CREATE TABLE IF NOT EXISTS public.cc_further_study_target (
    target_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    track VARCHAR(64) NOT NULL,
    target_school VARCHAR(255),
    target_major VARCHAR(255),
    target_region VARCHAR(255),
    target_stage VARCHAR(128),
    status VARCHAR(64) NOT NULL,
    target_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_further_target_user_track
    ON public.cc_further_study_target (user_id, track, updated_at DESC);

CREATE TABLE IF NOT EXISTS public.cc_further_study_record (
    record_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    track VARCHAR(64) NOT NULL,
    record_type VARCHAR(64) NOT NULL,
    target_id VARCHAR(64),
    title VARCHAR(255),
    status VARCHAR(64) NOT NULL,
    target_school VARCHAR(255),
    target_major VARCHAR(255),
    target_region VARCHAR(255),
    exam_or_deadline_date DATE,
    request_json TEXT,
    result_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_further_record_user_track
    ON public.cc_further_study_record (user_id, track, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_cc_further_record_user_type
    ON public.cc_further_study_record (user_id, record_type, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_cc_further_record_user_status
    ON public.cc_further_study_record (user_id, status, updated_at DESC);

CREATE TABLE IF NOT EXISTS public.cc_further_study_material (
    material_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    track VARCHAR(64) NOT NULL,
    record_id VARCHAR(64),
    material_type VARCHAR(64) NOT NULL,
    title VARCHAR(255),
    status VARCHAR(64) NOT NULL,
    file_key VARCHAR(255),
    content_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_further_material_user_track
    ON public.cc_further_study_material (user_id, track, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_cc_further_material_user_record
    ON public.cc_further_study_material (user_id, record_id, updated_at DESC);

CREATE TABLE IF NOT EXISTS public.cc_further_study_event (
    event_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    track VARCHAR(64) NOT NULL,
    record_id VARCHAR(64),
    event_type VARCHAR(64) NOT NULL,
    summary VARCHAR(500),
    event_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_further_event_user_record
    ON public.cc_further_study_event (user_id, record_id, created_at ASC);

CREATE INDEX IF NOT EXISTS idx_cc_further_event_user_track
    ON public.cc_further_study_event (user_id, track, created_at DESC);
