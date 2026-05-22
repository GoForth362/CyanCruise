## Context

IPD is a Spring Boot 3.5 / Java 17 backend with uni-app and Vue frontends. CyanCruise is a Kingdee Cosmic second-development project using Gradle and JDK 1.8. The migration therefore cannot be treated as framework-level porting; it must extract business behavior and rebuild it within CyanCruise constraints.

The primary IPD product input is `F:\Project\IPD\AI_PRODUCT_HANDOFF.md`. Additional source evidence comes from IPD backend controllers, services, model entities, frontend pages, and Flyway migrations.

## Goals

- Establish an OpenSpec baseline for IPD-to-CyanCruise migration.
- Preserve the CareerLoop core business loop as the product spine.
- Define evidence, ownership, and validation requirements for future migration changes.
- Prevent accidental direct migration of incompatible Spring Boot, Java 17, JPA, Flyway, Vue, or uni-app implementation details.

## Non-Goals

- No runtime code changes in this baseline.
- No data model creation in this baseline.
- No UI implementation in this baseline.
- No full migration of IPD authentication, administration, notification, or content features in this baseline.

## Source Inputs

| Source | Path | Use |
| --- | --- | --- |
| Product handoff | `F:\Project\IPD\AI_PRODUCT_HANDOFF.md` | Product positioning, main loop, completed phases |
| Backend services | `F:\Project\IPD\backend\src\main\java\com\group1\career\service` | Business orchestration and rules |
| Backend controllers | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller` | API semantics |
| Backend entities | `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity` | Domain objects and persistence concepts |
| Database migrations | `F:\Project\IPD\backend\src\main\resources\db\migration` | Field and table semantics |
| User frontend | `F:\Project\IPD\frontend\src` | User journeys, pages, copy, API contracts |
| Admin frontend | `F:\Project\IPD\admin-frontend\src` | Management capabilities and admin surfaces |

## Target Structure

| CyanCruise area | Ownership |
| --- | --- |
| `docs/` | Migration inventory, mapping, and human-readable planning |
| `openspec/changes/` | Active OpenSpec proposals, specs, designs, and tasks |
| `openspec/specs/` | Archived long-lived capability specifications |
| `datamodel/` | Cosmic data models for migrated entities |
| `code/base/v620-cc001-base-common/` | Shared models, constants, and contracts |
| `code/base/v620-cc001-base-helper/` | Reusable helpers and adapters |
| `code/cloud01/v620-cc001-cloud01-app01/` | Business application implementation |
| `webapp/` | User-facing page resources |

## Migration Strategy

1. Baseline the source product loop and migration rules.
2. Create one OpenSpec change per cohesive capability.
3. For each capability, document IPD source evidence and target CyanCruise ownership.
4. Extract business rules and data semantics from IPD.
5. Rebuild behavior using Cosmic-compatible patterns and JDK 8-compatible Java.
6. Validate with OpenSpec and Gradle before archive.

## Risks

- IPD source files contain some terminal-displayed mojibake for Chinese text; future work should inspect files with UTF-8-aware tools before copying user-facing text.
- IPD depends on frameworks and Java versions that are incompatible with CyanCruise.
- Migrating too much at once could obscure the core CareerLoop business loop.

## Decisions

- The first implementation work after this baseline should start with P0 capabilities.
- The recommended first implementation change is `migrate-career-profile-onboarding`.
- AI 今日任务 should initially migrate as deterministic/rule-driven recommendation behavior before introducing more autonomous agent behavior.

