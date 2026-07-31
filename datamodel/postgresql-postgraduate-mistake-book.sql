CREATE TABLE IF NOT EXISTS cc_postgraduate_mistake_book (
    mistake_id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    subject VARCHAR(255),
    question_text TEXT NOT NULL,
    wrong_answer TEXT,
    result_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS cc_postgraduate_mistake_book_user_updated_idx
    ON cc_postgraduate_mistake_book (user_id, updated_at DESC);
