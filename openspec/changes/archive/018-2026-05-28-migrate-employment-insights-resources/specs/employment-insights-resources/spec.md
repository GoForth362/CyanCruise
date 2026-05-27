## ADDED Requirements

### Requirement: Employment insight profile matching

CyanCruise SHALL provide a CareerLoop employment insight contract that derives school, major, and target role from the user profile context and uses them to select relevant employment records and public resource signals.

#### Scenario: User has supported school and target role

- **WHEN** a user with a supported school, major, and target role requests employment insight
- **THEN** the system SHALL return school, major, targetRole, matchLabel, summary, destinationHighlights, sourceCount, updatedAt, trend, coverage, and sources based on matching employment records

#### Scenario: Target role is missing

- **WHEN** a user has school and major data but no usable target role in the profile context
- **THEN** the system SHALL return an insight response that asks the user to complete the target role and SHALL NOT fabricate role-specific destination conclusions

### Requirement: Supported school boundary

The employment insight capability SHALL explicitly distinguish supported schools, missing school data, and unsupported schools. The system MUST NOT substitute another school's employment data for the user's school.

#### Scenario: School is missing

- **WHEN** the user profile has no usable school
- **THEN** the response SHALL identify that school data is required and SHALL return no employment rate, postgraduate rate, trend, or source-derived destination conclusion

#### Scenario: School is unsupported

- **WHEN** the user profile school is not in the supported school list
- **THEN** the response SHALL mark the school as unsupported and SHALL NOT return employment metrics from a different school

#### Scenario: School alias is recognized

- **WHEN** the user profile contains a known alias or variant for a supported school
- **THEN** the system SHALL normalize it to the canonical school name before matching records

### Requirement: Source-backed metrics and trends

The employment insight capability SHALL expose employment rate, postgraduate rate, latest year, trend points, and source excerpts only when those values are present in traceable source records.

#### Scenario: Traceable metrics exist

- **WHEN** selected employment records include employment or postgraduate rates with source year and source URL
- **THEN** the response SHALL include the latest metrics, trend points by year, and source items that identify title, URL, source type, year, keywords, excerpt, and fetchedAt

#### Scenario: Metrics are absent

- **WHEN** selected records do not contain recognizable employment or postgraduate rates
- **THEN** the response SHALL omit numeric metric values and SHALL include a summary or highlight that explains the data is unavailable or pending verification

### Requirement: Coverage audit

The employment insight capability SHALL provide coverage audit items for supported schools and recent graduate years so reviewers can distinguish complete, partial, missing, and manually reviewed source coverage.

#### Scenario: Full source coverage is available

- **WHEN** an official source record has core metrics and destination description for a school-year
- **THEN** the coverage item SHALL use a verified-full status and include a reason and source URL

#### Scenario: Source coverage is missing

- **WHEN** no record exists for a supported school-year
- **THEN** the coverage item SHALL use a missing status and include a reason that no verifiable public source was found

#### Scenario: Source requires manual review

- **WHEN** only an aggregate page, entry link, or incomplete source is available
- **THEN** the coverage item SHALL use partial or needs-manual-review status and SHALL NOT be treated as a fully verified employment report

### Requirement: Career resource feed

CyanCruise SHALL provide a CareerLoop resource feed contract for webapp consumption that can expose article cards, video cards, consultation/tip cards, and career path cards without requiring IPD frontend or crawler runtime.

#### Scenario: Resource feed is requested

- **WHEN** the webapp requests CareerLoop resources
- **THEN** the system SHALL return resource cards with stable identifiers, title, summary or body, category or keyword, source URL, image URL when available, and type-specific fields such as video duration or career path id

#### Scenario: Resource feed has no configured content

- **WHEN** no resource records are available
- **THEN** the system SHALL return an empty or unavailable resource state that keeps the webapp navigable and SHALL NOT fail the CareerLoop workbench

#### Scenario: User id is available for resources

- **WHEN** a user id is available to the resource feed
- **THEN** the system MAY use it for stable daily ordering or personalization but SHALL keep anonymous read access possible for non-user-owned resource cards

### Requirement: WebAPI and route contract mapping

The migration SHALL define Cosmic WebAPI and webapp route/API mapping for employment insights and resource entries.

#### Scenario: WebAPI contract is reviewed

- **WHEN** reviewers inspect the migration artifacts
- **THEN** they SHALL find the employment insight WebAPI contract, resource feed WebAPI contract, required user identity semantics, DTO fields, fallback states, and related webapp route keys

#### Scenario: Existing CareerLoop workbench integrates the entry

- **WHEN** the webapp workbench adds employment insight or resource entry points
- **THEN** the route/API map SHALL identify how those entries consume the new contracts without changing existing profile, today-action, career-plan, interview, resume, assessment, or assistant contracts

### Requirement: Migration boundary for employment insights and resources

The migration SHALL rebuild employment insight and resource semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Boot, JPA, Flyway, Java 17 HTTP, PDFBox, Redis, Bilibili crawler, Vue, uni-app, Pinia/store, or mini-program runtime implementations.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependency changes are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The employment insight and resource migration SHALL include verification and documentation proving the OpenSpec contract, implementation, webapp mapping, and migration map are aligned.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results
