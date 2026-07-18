-- CyanCruise 每日路线任务。
-- 这是既有业务对象 v620_cc_career_task 在 PostgreSQL 验收环境中的对应存储，
-- 不改变职业计划和智能体输出结构。

CREATE TABLE IF NOT EXISTS cc_career_task (
    task_id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    task_key VARCHAR(128) NOT NULL,
    title VARCHAR(1024) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    status VARCHAR(64) NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    parent_task_id VARCHAR(128),
    sub_index INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_career_task_user_date
    ON cc_career_task (user_id, due_date, sub_index);

CREATE INDEX IF NOT EXISTS idx_cc_career_task_user_source
    ON cc_career_task (user_id, task_key, status);
