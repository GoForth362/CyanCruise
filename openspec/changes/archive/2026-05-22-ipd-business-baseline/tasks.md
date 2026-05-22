## 1. Baseline Documentation

- [x] 1.1 Create `docs/ipd-business-inventory.md` with IPD project positioning, core loop, source structure, and capability inventory.
- [x] 1.2 Create `docs/ipd-to-cyancruise-migration-map.md` with source-to-target capability mapping and recommended change order.
- [x] 1.3 Create `AGENTS.md` with CyanCruise project constraints, module boundaries, IPD migration rules, and verification commands.

## 2. OpenSpec Baseline

- [x] 2.1 Create proposal for `ipd-business-baseline`.
- [x] 2.2 Add `ipd-business-loop` specification.
- [x] 2.3 Add `migration-governance` specification.
- [x] 2.4 Add `cosmic-rebuild-boundary` specification.
- [x] 2.5 Add design notes for source inputs, target structure, migration strategy, risks, and decisions.

## 3. Validation

- [x] 3.1 Run `openspec validate ipd-business-baseline --strict`.
- [x] 3.2 Review `openspec status --change ipd-business-baseline`.
- [x] 3.3 Confirm no runtime code, data model, web resource, or build script was changed.

## 4. Recommended Next Change

- [ ] 4.1 Create `migrate-career-profile-onboarding` as the first implementation change.
- [ ] 4.2 Use IPD sources `AgentProfileService`, `UserProfileSnapshotService`, onboarding frontend files, and user profile DTO/entity files as source evidence.
- [ ] 4.3 Define CyanCruise target data model and business service ownership before implementation.
