-- CyanCruise local PostgreSQL notice storage.
-- Fields align with the v620_cc_notice business object semantics:
-- notice_id -> v620_noticeid, user_id -> v620_userid, notice_type -> v620_noticetype,
-- title -> v620_title, content -> v620_content, link_route -> v620_linkroute,
-- status -> v620_status, admin_id -> v620_adminid, created_at -> v620_createdat,
-- read_at -> v620_readat.

CREATE TABLE IF NOT EXISTS public.cc_notice (
  notice_id VARCHAR(128) PRIMARY KEY,
  user_id VARCHAR(128) NOT NULL,
  notice_type VARCHAR(64) NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  link_route VARCHAR(255),
  status VARCHAR(64) NOT NULL DEFAULT 'sent',
  admin_id VARCHAR(128),
  created_at TIMESTAMP,
  read_at TIMESTAMP,
  payload_json JSONB NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cc_notice_user_status_created
  ON public.cc_notice (user_id, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cc_notice_type_created
  ON public.cc_notice (notice_type, created_at DESC);
