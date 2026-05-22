## Why

CyanCruise will rebuild business logic from the existing IPD/CareerLoop project, but the source and target projects use different architectures. A baseline change is needed now to capture the business loop, migration boundaries, and governance rules before implementation changes begin.

## What Changes

- Establish the IPD business loop as the source-of-truth for future CyanCruise migration work.
- Define how IPD capabilities map into CyanCruise modules and OpenSpec changes.
- Add migration governance requirements so future work migrates business behavior rather than framework-specific implementation.
- Add supporting documentation under `docs/` for the IPD business inventory and migration map.

## Capabilities

### New Capabilities
- `ipd-business-loop`: Defines the CareerLoop business loop and priority capabilities to preserve during the CyanCruise rebuild.
- `migration-governance`: Defines the required process and evidence for every IPD-to-CyanCruise migration change.
- `cosmic-rebuild-boundary`: Defines what may be migrated into the Cosmic/JDK 8 target and what must be redesigned.

### Modified Capabilities
- None.

## Impact

- Adds OpenSpec baseline artifacts under `openspec/changes/ipd-business-baseline/`.
- Adds migration planning documents under `docs/`.
- Adds repository-level development guidance in `AGENTS.md`.
- Does not change runtime code, database models, web resources, or build scripts.

