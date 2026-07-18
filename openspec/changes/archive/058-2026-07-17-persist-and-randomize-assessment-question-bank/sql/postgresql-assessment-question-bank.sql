-- 部署前在 CyanCruise PostgreSQL 业务 schema 中执行。
-- 脚本可重复执行，不会覆盖已有管理员题库配置。

CREATE TABLE IF NOT EXISTS cc_assessment_scale (
    scale_id BIGINT PRIMARY KEY,
    payload_json JSONB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cc_assessment_attempt (
    attempt_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    scale_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    payload_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cc_assessment_attempt_user_scale
    ON cc_assessment_attempt (user_id, scale_id, created_at DESC);
