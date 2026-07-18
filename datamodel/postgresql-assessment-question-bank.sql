-- CyanCruise 职业测评题库与固定作答批次。
-- 量表、题目、选项和管理员配置统一保存在量表 JSON 快照中；
-- 用户每次开始测评后，抽中的题目另存为不可变批次快照。

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
