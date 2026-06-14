-- CyanCruise profile storage tables for PostgreSQL.
-- Target database: cyancruise
-- Target schema: public
-- Suggested application user: cyancruise_app
-- This script intentionally contains no password or local environment path.

CREATE TABLE IF NOT EXISTS public.cc_profile_draft (
    user_id VARCHAR(128) PRIMARY KEY,
    identity_type VARCHAR(64),
    education_stage VARCHAR(64),
    school_major VARCHAR(255),
    resume_status VARCHAR(64),
    target_role VARCHAR(255),
    preference TEXT,
    route_intent VARCHAR(128),
    experience_text TEXT,
    draft_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_profile_draft_updated_at
    ON public.cc_profile_draft (updated_at);

CREATE TABLE IF NOT EXISTS public.cc_profile_snapshot (
    user_id VARCHAR(128) PRIMARY KEY,
    version INTEGER NOT NULL DEFAULT 1,
    target_role VARCHAR(255),
    snapshot_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_profile_snapshot_target_role
    ON public.cc_profile_snapshot (target_role);

CREATE INDEX IF NOT EXISTS idx_cc_profile_snapshot_updated_at
    ON public.cc_profile_snapshot (updated_at);

CREATE TABLE IF NOT EXISTS public.cc_profile_fact (
    user_id VARCHAR(128) NOT NULL,
    fact_key VARCHAR(128) NOT NULL,
    fact_value TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, fact_key)
);

CREATE INDEX IF NOT EXISTS idx_cc_profile_fact_key
    ON public.cc_profile_fact (fact_key);

CREATE TABLE IF NOT EXISTS public.cc_user_profile (
    user_id VARCHAR(128) PRIMARY KEY,
    personalization_level VARCHAR(64),
    completeness_score INTEGER,
    current_stage VARCHAR(128),
    target_role VARCHAR(255),
    profile_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_user_profile_target_role
    ON public.cc_user_profile (target_role);

CREATE INDEX IF NOT EXISTS idx_cc_user_profile_updated_at
    ON public.cc_user_profile (updated_at);
