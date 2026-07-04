-- CyanCruise admin governance PostgreSQL storage DDL.
-- Target: jdbc:postgresql://10.0.0.8:5432/cyancruise, schema public.
-- Execute manually as an account allowed to create tables in public.
-- Do not add real passwords to this file.

CREATE TABLE IF NOT EXISTS public.cc_admin_organization (
    org_id VARCHAR(128) PRIMARY KEY,
    code VARCHAR(128),
    name VARCHAR(255),
    active BOOLEAN,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE IF NOT EXISTS public.cc_admin_user (
    user_id VARCHAR(128) PRIMARY KEY,
    org_id VARCHAR(128),
    nickname VARCHAR(255),
    status VARCHAR(64),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_cc_admin_user_org
    ON public.cc_admin_user (org_id);

CREATE INDEX IF NOT EXISTS idx_cc_admin_user_status
    ON public.cc_admin_user (status);

CREATE TABLE IF NOT EXISTS public.cc_admin_career_path (
    path_id VARCHAR(128) PRIMARY KEY,
    code VARCHAR(128),
    name VARCHAR(255),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE IF NOT EXISTS public.cc_admin_career_node (
    node_id VARCHAR(128) PRIMARY KEY,
    path_id VARCHAR(128),
    parent_id VARCHAR(128),
    sort_order INTEGER,
    name VARCHAR(255),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE IF NOT EXISTS public.cc_admin_question (
    question_id VARCHAR(128) PRIMARY KEY,
    source VARCHAR(64),
    review_status VARCHAR(64),
    status VARCHAR(64),
    position VARCHAR(255),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_admin_question_review
    ON public.cc_admin_question (review_status, source);

CREATE TABLE IF NOT EXISTS public.cc_admin_content (
    content_id VARCHAR(128) PRIMARY KEY,
    type VARCHAR(64),
    title VARCHAR(255),
    pinned BOOLEAN NOT NULL DEFAULT false,
    hidden BOOLEAN NOT NULL DEFAULT false,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    published_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_admin_content_type
    ON public.cc_admin_content (type);

CREATE TABLE IF NOT EXISTS public.cc_admin_audit_log (
    audit_id VARCHAR(128) PRIMARY KEY,
    admin_id VARCHAR(128),
    action VARCHAR(128),
    target_type VARCHAR(128),
    target_id VARCHAR(128),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_admin_audit_created
    ON public.cc_admin_audit_log (created_at DESC);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE
    public.cc_admin_organization,
    public.cc_admin_user,
    public.cc_admin_career_path,
    public.cc_admin_career_node,
    public.cc_admin_question,
    public.cc_admin_content,
    public.cc_admin_audit_log
TO cyancruise_app;
