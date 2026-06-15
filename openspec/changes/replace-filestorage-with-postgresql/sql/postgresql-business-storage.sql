-- CyanCruise PostgreSQL business-state storage DDL.
-- Target: jdbc:postgresql://10.0.0.8:5432/cyancruise, schema public.
-- Execute manually as an account allowed to create tables in public.
-- Do not add real passwords to this file.

CREATE TABLE IF NOT EXISTS public.cc_career_plan (
    user_id VARCHAR(128) PRIMARY KEY,
    target_role VARCHAR(255),
    version INTEGER,
    generated_from VARCHAR(128),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_career_plan_updated_at
    ON public.cc_career_plan (updated_at);

CREATE TABLE IF NOT EXISTS public.cc_resume_record (
    resume_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    title VARCHAR(255),
    target_job VARCHAR(255),
    file_key VARCHAR(512),
    version VARCHAR(64),
    status VARCHAR(64),
    diagnosis_score INTEGER,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_resume_record_user_updated
    ON public.cc_resume_record (user_id, updated_at DESC);

CREATE TABLE IF NOT EXISTS public.cc_resume_diagnosis (
    resume_id BIGINT PRIMARY KEY,
    overall_score INTEGER,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS public.cc_resume_keyword_status (
    resume_id BIGINT PRIMARY KEY,
    status VARCHAR(64),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS public.cc_interview_session (
    interview_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    resume_id BIGINT,
    target_role VARCHAR(255),
    status VARCHAR(64),
    mode VARCHAR(64),
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    final_score INTEGER,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_interview_session_user_started
    ON public.cc_interview_session (user_id, started_at DESC, interview_id DESC);

CREATE TABLE IF NOT EXISTS public.cc_interview_message (
    message_id BIGSERIAL PRIMARY KEY,
    interview_id BIGINT NOT NULL REFERENCES public.cc_interview_session(interview_id) ON DELETE CASCADE,
    role VARCHAR(64),
    created_at TIMESTAMPTZ,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_cc_interview_message_interview_order
    ON public.cc_interview_message (interview_id, created_at ASC, message_id ASC);

CREATE TABLE IF NOT EXISTS public.cc_assistant_chat_session (
    session_id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    persona VARCHAR(64),
    title VARCHAR(255),
    model_name VARCHAR(128),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_cc_assistant_chat_session_user_updated
    ON public.cc_assistant_chat_session (user_id, updated_at DESC, session_id DESC);

CREATE TABLE IF NOT EXISTS public.cc_assistant_chat_message (
    msg_id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES public.cc_assistant_chat_session(session_id) ON DELETE CASCADE,
    role VARCHAR(64),
    created_at TIMESTAMPTZ,
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_cc_assistant_chat_message_session_order
    ON public.cc_assistant_chat_message (session_id, created_at ASC, msg_id ASC);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE
    public.cc_career_plan,
    public.cc_resume_record,
    public.cc_resume_diagnosis,
    public.cc_resume_keyword_status,
    public.cc_interview_session,
    public.cc_interview_message,
    public.cc_assistant_chat_session,
    public.cc_assistant_chat_message
TO cyancruise_app;

GRANT USAGE, SELECT ON SEQUENCE
    public.cc_resume_record_resume_id_seq,
    public.cc_interview_session_interview_id_seq,
    public.cc_interview_message_message_id_seq,
    public.cc_assistant_chat_session_session_id_seq,
    public.cc_assistant_chat_message_msg_id_seq
TO cyancruise_app;
