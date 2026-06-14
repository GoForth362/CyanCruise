## PostgreSQL Database Readiness

Verification date: 2026-06-14

Verified target:
- PostgreSQL server: `10.0.0.8:5432`
- Database: `cyancruise`
- Schema: `public`
- Application user: `cyancruise_app`

Verified results:
- `current_user` returns `cyancruise_app` when using the CyanCruise application connection.
- `SELECT COUNT(*)` succeeds for all required profile tables:
  - `public.cc_profile_draft`
  - `public.cc_profile_snapshot`
  - `public.cc_profile_fact`
  - `public.cc_user_profile`
- `INSERT`, `SELECT`, and `DELETE` succeed on `public.cc_profile_fact` using the application user.
- Focused non-live Gradle tests compile and pass for PostgreSQL profile storage configuration and factory fallback behavior.
- Optional live PostgreSQL storage tests are present and are gated by `cc001.profile.postgresql.liveTest=true`.
- Live PostgreSQL storage test passed with `cyancruise_app` against `jdbc:postgresql://10.0.0.8:5432/cyancruise`.
- `openspec validate connect-career-profile-postgresql-storage --strict` passes.
- JDK 8 `.\gradlew.bat :v620-cc001-cloud01-app01:test` passes.
- JDK 8 `.\gradlew.bat clean build` passes.

Security notes:
- No database password or private credential is recorded in this artifact.
- The application password SHALL be supplied through local or deployment configuration.
- Manual SQL table creation has been used as the default initialization path.
