CREATE TABLE IF NOT EXISTS cc_study_center_plan (
    user_id VARCHAR(128) NOT NULL,
    direction VARCHAR(64) NOT NULL,
    plan_json JSONB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, direction)
);

CREATE TABLE IF NOT EXISTS cc_study_center_daily_task (
    user_id VARCHAR(128) NOT NULL,
    task_id VARCHAR(255) NOT NULL,
    direction VARCHAR(64) NOT NULL,
    plan_date DATE,
    task_json JSONB NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, direction, task_id)
);

UPDATE cc_study_center_plan
SET direction = 'POSTGRADUATE'
WHERE direction IS NULL OR btrim(direction) = '';

UPDATE cc_study_center_daily_task
SET direction = 'POSTGRADUATE'
WHERE direction IS NULL OR btrim(direction) = '';

ALTER TABLE cc_study_center_plan ALTER COLUMN direction SET NOT NULL;
ALTER TABLE cc_study_center_daily_task ALTER COLUMN direction SET NOT NULL;

DO $$
DECLARE old_key text;
BEGIN
    SELECT conname INTO old_key
    FROM pg_constraint
    WHERE conrelid = 'cc_study_center_plan'::regclass
      AND contype = 'p'
      AND NOT EXISTS (
          SELECT 1 FROM unnest(conkey) AS key_no(attnum)
          JOIN pg_attribute attr
            ON attr.attrelid = conrelid AND attr.attnum = key_no.attnum
          WHERE attr.attname = 'direction'
      );
    IF old_key IS NOT NULL THEN
        EXECUTE 'ALTER TABLE cc_study_center_plan DROP CONSTRAINT ' || quote_ident(old_key);
    END IF;
END $$;

DO $$
DECLARE old_key text;
BEGIN
    SELECT conname INTO old_key
    FROM pg_constraint
    WHERE conrelid = 'cc_study_center_daily_task'::regclass
      AND contype = 'p'
      AND NOT EXISTS (
          SELECT 1 FROM unnest(conkey) AS key_no(attnum)
          JOIN pg_attribute attr
            ON attr.attrelid = conrelid AND attr.attnum = key_no.attnum
          WHERE attr.attname = 'direction'
      );
    IF old_key IS NOT NULL THEN
        EXECUTE 'ALTER TABLE cc_study_center_daily_task DROP CONSTRAINT ' || quote_ident(old_key);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS idx_cc_study_plan_user_direction
    ON cc_study_center_plan (user_id, direction);

CREATE UNIQUE INDEX IF NOT EXISTS idx_cc_study_daily_user_direction_task
    ON cc_study_center_daily_task (user_id, direction, task_id);

CREATE INDEX IF NOT EXISTS idx_cc_study_daily_user_direction_date
    ON cc_study_center_daily_task (user_id, direction, plan_date);

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
