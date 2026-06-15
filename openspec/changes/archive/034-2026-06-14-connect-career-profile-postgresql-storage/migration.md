## PostgreSQL Profile Storage Migration Notes

### Source And Target

- Historical IPD source: `F:\Project\IPD`
- CyanCruise target module: `code/cloud01/v620-cc001-cloud01-app01/`
- Storage boundary: `CareerProfileStorage`
- PostgreSQL adapter: `PostgresqlCareerProfileStorage`
- Configuration factory: `CareerProfileStorageFactory`

### PostgreSQL Target

- Server: `10.0.0.8:5432`
- Database: `cyancruise`
- Schema: `public`
- Suggested application user: `cyancruise_app`
- Password handling: local or deployment configuration only; no password is recorded in OpenSpec, source code, route metadata, or committed examples.

### Table Mapping

| CyanCruise data | PostgreSQL table | Structured fields | Payload field |
| --- | --- | --- | --- |
| Profile draft | `public.cc_profile_draft` | `user_id`, route-entry fields, `target_role`, `updated_at` | `draft_json` |
| Profile snapshot | `public.cc_profile_snapshot` | `user_id`, `version`, `target_role`, `updated_at` | `snapshot_json` |
| Profile facts | `public.cc_profile_fact` | `user_id`, `fact_key`, `fact_value`, `updated_at` | none |
| Unified profile | `public.cc_user_profile` | `user_id`, `personalization_level`, `completeness_score`, `current_stage`, `target_role`, `updated_at` | `profile_json` |

### Configuration

The PostgreSQL adapter is used only when all required properties are present:

```properties
cc001.profile.storage.adapter=postgresql
cc001.profile.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise
cc001.profile.postgresql.username=cyancruise_app
cc001.profile.postgresql.password=<password>
cc001.profile.postgresql.schema=public
cc001.profile.postgresql.initialize=false
```

The same values may be supplied through environment variables for local live tests or deployment:

```text
CC001_PROFILE_STORAGE_ADAPTER=postgresql
CC001_PROFILE_POSTGRESQL_URL=jdbc:postgresql://10.0.0.8:5432/cyancruise
CC001_PROFILE_POSTGRESQL_USERNAME=cyancruise_app
CC001_PROFILE_POSTGRESQL_PASSWORD=<password>
CC001_PROFILE_POSTGRESQL_SCHEMA=public
CC001_PROFILE_POSTGRESQL_INITIALIZE=false
```

If the adapter is not set to `postgresql`, or any required connection property is missing, CyanCruise uses file profile storage as the fallback.

### Initialization

Manual SQL is the default table creation path:

- SQL artifact: `openspec/changes/connect-career-profile-postgresql-storage/sql/postgresql-profile-storage.sql`
- The script creates four tables and supporting indexes.
- The script contains no password, grant statement, or local private path.

Program initialization is optional and disabled by default. When `cc001.profile.postgresql.initialize=true`, the adapter only uses `CREATE TABLE IF NOT EXISTS` and `CREATE INDEX IF NOT EXISTS`; it does not delete, truncate, rename, migrate, or otherwise modify existing data.

### Rollback

To roll back CyanCruise profile storage to file storage:

1. Remove or change `cc001.profile.storage.adapter=postgresql`.
2. Restart the application so `CareerProfileStorageFactory` selects `FileCareerProfileStorage`.
3. Keep PostgreSQL tables intact for inspection or future re-enable; do not drop tables as part of application rollback.

### Verification

- OpenSpec strict validation: `openspec validate connect-career-profile-postgresql-storage --strict`
- Focused non-live tests:
  - `.\gradlew.bat :v620-cc001-cloud01-app01:test --tests "v620.cc001.cloud01.app01.mservice.PostgresqlProfileStorageConfigTest"`
- Optional live PostgreSQL tests require explicit properties, including `cc001.profile.postgresql.liveTest=true`.
- Optional live PostgreSQL tests may also be enabled with `CC001_PROFILE_POSTGRESQL_LIVE_TEST=true`.
- Full JDK 8 validation: `.\gradlew.bat clean build`

### Temporarily Not Migrated

- No direct migration of IPD Spring Boot, JPA, Flyway, repository, Vue, or uni-app implementation.
- No database storage replacement for resume, assessment, career plan, today action, interview, assistant chat, notification, or admin modules in this change.
- No production data backfill from file storage to PostgreSQL in this change.
