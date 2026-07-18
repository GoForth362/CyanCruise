CREATE TABLE IF NOT EXISTS cc_study_center_plan (
    user_id VARCHAR(128) PRIMARY KEY,
    direction VARCHAR(64) NOT NULL,
    plan_json JSONB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cc_study_center_daily_task (
    user_id VARCHAR(128) NOT NULL,
    task_id VARCHAR(255) NOT NULL,
    direction VARCHAR(64),
    plan_date DATE,
    task_json JSONB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, task_id)
);

CREATE INDEX IF NOT EXISTS idx_cc_study_daily_user_date
    ON cc_study_center_daily_task (user_id, plan_date);

CREATE TABLE IF NOT EXISTS cc_study_center_material (
    material_id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    direction VARCHAR(64) NOT NULL,
    material_type VARCHAR(64),
    object_key VARCHAR(1024) NOT NULL,
    original_filename VARCHAR(512),
    extraction_status VARCHAR(64),
    payload_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cc_study_material_user_direction
    ON cc_study_center_material (user_id, direction, updated_at DESC);
