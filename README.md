# GroundWork

GroundWork is an offline-first Android app for field operations teams working in low-connectivity environments.

The long-term product direction includes site management, field notes, reports, local sync visibility, and conflict-aware offline workflows. The current implementation provides a local-first foundation in which field notes can optionally belong to sites while remaining available for fast unassigned capture, and every local change is already tracked with sync metadata so a sync layer can be added behind the repositories.

## Project Goal

The goal of this project is not to build another generic notes app. The goal is to demonstrate senior Android engineering judgment through a small but realistic product surface: local-first persistence, predictable UI state, clear architecture boundaries, and a design that can later support sync and conflict resolution without rewriting the app.

The current version focuses on getting the local foundation right: creating, editing, deleting, listing, searching, and filtering field notes and sites using a clean Compose UI, a Room-backed data layer, ViewModel-managed state, and repository contracts between the domain and data layers.

The sync groundwork is in place: records carry pending/failed/synced state, deletes are soft until they sync, and repository writes are transactional. The next step is the sync simulation itself (fake remote source, sync worker, retry), followed by conflict resolution, attachments, and performance work.

## Tech Stack

- Kotlin 2.4, Jetpack Compose with Material 3
- Room 2.8 with exported schemas and tested migrations
- Hilt for dependency injection, including assisted injection for route-scoped ViewModels
- Navigation Compose 2.10 with type-safe `@Serializable` routes
- Coroutines and Flow for observable local data
- JUnit 4, Turbine, kotlinx-coroutines-test, Room testing, and Compose UI tests
- AGP 9.4, Gradle 9.8, minSdk 30, targetSdk 37, R8 shrinking for release builds
- GitHub Actions for unit tests, debug builds, and emulator-based instrumented tests

## Seniority Signals

This project is designed to showcase:

- Offline-first architecture with local persistence as the foundation
- Clear separation between domain, data, dependency injection, navigation, and feature UI layers
- Explicit UI state modeling with ViewModels and Compose
- Repository-driven data access instead of direct database access from UI
- Route/screen separation for previewable, stateless Compose UI
- Type-safe navigation, with typed routes passed into ViewModels through Hilt assisted injection
- Sync state modeled as domain behavior (`SyncMetadata`) rather than scattered flags
- Data-integrity rules enforced at the repository layer: transactional read-then-write, soft deletes, and protection against reviving deleted records
- Typed UI errors resolved to string resources in the UI, keeping ViewModels free of display text
- Injected `Clock` and `IdGenerator` so time- and ID-dependent behavior is tested with exact values
- Scope control: field-note/site association was added only after both local foundations were stable, and sync is being built in layers (metadata first, sync engine next)
- A roadmap that grows complexity intentionally instead of adding features randomly
- Local and instrumented tests for the domain model, mappers, migrations, DAOs, repositories, ViewModels, UI state models, and key Compose interactions, all run in CI

## Current Progress

Git tags are created only for stable checkpoints. The latest stable tag is `v0.8.0`, representing the field-note/site relationship, site-aware creation and editing flows, Site Detail, and assigned/unassigned test coverage. `main` also contains the untagged v0.9.0 work described below.

### v0.1 — Project Setup
- Created Android project
- Added GitHub repository
- Added initial README
- Added feature-oriented package structure
- Added Room, KSP, and Hilt dependency setup
- Added Android Studio/local build files to `.gitignore`

### v0.2 — Local Field Notes Foundation
- Added `FieldNote`, `FieldNoteId`, and `FieldNoteStatus` domain models
- Added `FieldNoteRepository` domain contract
- Added Room persistence with `FieldNoteEntity`, `FieldNoteDao`, and `GroundWorkDatabase`
- Configured Room schema export and committed the generated schema
- Added mapper functions between `FieldNoteEntity` and `FieldNote`
- Implemented `OfflineFirstFieldNoteRepository`
- Wired local database, DAO, and repository using Hilt

### v0.3 — Field Notes UI State and List Screen
- Added `FieldNotesUiState`
- Added `FieldNotesViewModel` that observes repository data and exposes UI state
- Added `FieldNotesRoute` as the ViewModel-connected container
- Added stateless `FieldNotesListScreen`
- Added loading, empty, error, and content UI states
- Added `FieldNoteCard` for rendering field note previews
- Connected the field notes route from `MainActivity`

### v0.4 — Create, Edit, and Delete Field Notes
- Added `FieldNoteEditorUiState` and `FieldNoteEditorViewModel`
- Added a Stitch-inspired `FieldNoteEditorScreen` with top bar, local draft/status card, title input, observations input, error state, and bottom save action
- Added editor previews for empty, filled, saving, error, and dark-mode states
- Added FAB entry point from the field notes list
- Added navigation between the list and editor screens
- Added create flow for saving new field notes locally
- Added edit flow for opening an existing field note, loading its title/body, and saving updates without creating duplicates
- Added delete flow for removing existing field notes from the editor
- Added discard flow for abandoning unsaved drafts without touching persisted data
- Preserved `createdAt` when editing existing notes and updated `updatedAt` on save

### v0.5 — Search, Filtering, and First Tests
- Added status filtering for field notes using Material filter chips
- Added search by field note title and body
- Combined search query and status filter state in `FieldNotesViewModel`
- Added no-match UI for active search/filter criteria
- Added search UI polish, including leading search icon and clear-search action
- Added preview coverage for content, loading, empty, error, no-match, and dark-mode states
- Added mapper tests for `FieldNoteEntity` ↔ `FieldNote`
- Added `FieldNotesListViewModel` tests for repository data, search, status filtering, combined search/filter behavior, body search, and error state
- Updated Android build tooling and Gradle wrapper

### v0.6 — Local Unit Tests
- Added DAO tests for inserting, observing, loading, deleting, searching, and ordering field notes
- Added in-memory Room database coverage for DAO and repository behavior
- Added repository tests using an in-memory Room database
- Added `FieldNoteEditorViewModel` tests for create, edit, delete, and discard flows

### v0.6.1 — CI and Test Workflow Polish
- Added GitHub Actions workflow for Android CI
- Added local unit test job for `testDebugUnitTest`
- Added debug build job through `assembleDebug`
- Added instrumented Android test job for `connectedDebugAndroidTest`
- Verified DAO, repository, and ViewModel tests run in CI

### v0.7.0 — Sites Foundation
- Renamed `JobSite` direction to the simpler product language `Site`
- Added `Site`, `SiteId`, `SiteStatus`, and `SitePriority` domain models
- Added `SiteRepository` domain contract
- Added Room persistence with `SiteEntity` and `SiteDao`
- Added mapper functions between `SiteEntity` and `Site`
- Implemented `OfflineFirstSiteRepository`
- Added Hilt bindings for site repository dependencies
- Added Sites list screen with loading, empty, error, content, and no-match states
- Added site search and status/priority filtering
- Added Sites editor screen for creating, editing, deleting, and discarding sites
- Added bottom navigation between Field Notes and Sites
- Reorganized Field Notes and Sites UI into list/editor feature packages
- Moved shared editor back button into a reusable UI component
- Added localized relative-time formatting for list cards
- Added DAO and repository coverage for field notes and sites
- Added UI state tests for Field Notes and Sites list/editor state models

### v0.8.0 — Field Note and Site Relationship
- Added an optional `siteId` relationship from field notes to sites
- Added a Room foreign key and index while preserving unassigned notes and converting orphaned migrated relationships to unassigned
- Preserved field notes as unassigned when their associated site is deleted
- Kept field notes usable without a site for fast capture
- Added associated-site selection, reassignment, and removal in the field-note editor
- Added site-preselected field-note creation from Site Detail
- Added Site Detail with site-scoped field notes and per-site note counts
- Added site labels and site-name search to the main Field Notes list
- Added assigned/unassigned coverage across migration, DAO, repository, ViewModel, UI state, and Compose UI layers

### v0.9.0 — Sync Simulation (in progress, untagged)

Sync groundwork:
- Added `SyncMetadata` to field notes and sites, with `SYNCED`, `PENDING_CREATE`, `PENDING_UPDATE`, `PENDING_DELETE`, and `FAILED` states
- Modeled state transitions as domain behavior: editing an unsynced create stays a create, a failed create is retried as a create, and deleting a never-synced record removes it outright
- Recorded structured sync failures with the failed operation, message, and failure time
- Made deletes of synced records soft: rows are marked `PENDING_DELETE` and hidden from the UI until sync completes; failed deletes stay hidden and can be retried
- Rejected updates to records that are pending or failed deletes so a save cannot revive a deleted record
- Unassigned a deleted site's visible field notes and marked them pending update in the same transaction, since soft deletes do not trigger `ON DELETE SET NULL`
- Ran every repository read-then-write path in a database transaction
- Defined the "not deleted" visibility rule once as a shared SQL fragment used by all UI-facing queries
- Decoded unknown persisted enum values to safe defaults in the data layer
- Added Room migrations 2→3, 3→4, and 4→5 for sync columns, with migration tests

Architecture and quality:
- Moved navigation to type-safe `@Serializable` routes; Site Detail now always has a site ID
- Passed typed routes into editor and detail ViewModels through Hilt assisted injection instead of `SavedStateHandle` lookups
- Replaced hard-coded ViewModel error strings with per-screen error types resolved from string resources
- Injected `java.time.Clock` and an `IdGenerator` into the editor ViewModels
- Trimmed site names on save and showed a "Site name is required" hint once the name is edited and left blank
- Simplified site status to Active/Archived and site priority to Low/Normal/High

Build and CI:
- Upgraded AGP, Gradle, Kotlin, KSP, Room, Compose, and Navigation
- Enabled R8 code and resource shrinking for release builds
- Moved the instrumented test job to Ubuntu with KVM so emulator tests run in CI again
- Cancelled superseded CI runs for the same branch or pull request

Remaining for v0.9.0:
- Fake remote data source with controllable failures
- Sync worker that processes pending changes and records success or failure
- Guard against losing a local edit made while an upload is in flight
- Manual retry and a sync status indicator in the UI

## MVP Scope

### In scope for v1
- Create, edit, delete, list, and search field notes
- Persist field notes locally using Room
- Manage sites locally using Room
- Model UI state explicitly in ViewModels
- Keep composables mostly stateless
- Keep Room entities separate from domain models
- Associate field notes with sites after both local foundations are stable
- Build architecture that can support sites and sync later

### Out of scope for v1
- Real backend
- Authentication
- Attachments
- Maps
- GPS
- Push notifications
- Multi-user collaboration
- Real sync
- Conflict resolution

## Architecture Decisions

### Local-first foundation

The app starts with local persistence before any remote sync. This keeps the first version focused on reliable state and data flow instead of prematurely introducing backend complexity.

### Domain-first modeling

The app starts with a small domain model before introducing Room or UI implementation details. `FieldNote`, `FieldNoteId`, `FieldNoteStatus`, `Site`, `SiteId`, `SiteStatus`, and `SitePriority` describe the core app concepts independently of persistence, navigation, or Compose. Repository contracts define how the rest of the app accesses local data without depending directly on the database layer.

### Room as future source of truth

Even before sync exists, Room is treated as the durable source for app data. The UI reads app state through repositories and ViewModels rather than directly from the database.

### Field notes and sites before association

Field notes were implemented first as the smallest useful slice of the larger GroundWork concept. Sites were added next as a separate local foundation, with their own domain model, Room persistence, repository, list/editor UI, and tests.

The relationship between field notes and sites was introduced only after both foundations were stable. The relationship is optional so field notes can belong to a site without sacrificing fast unassigned capture.

### Route and screen separation

Compose screens separate ViewModel-connected routes from stateless screen content. Routes collect ViewModel state, while screens receive state and callbacks. This keeps the screens previewable, easier to test, and independent of Hilt.

### Repository-driven UI state

ViewModels observe repository contracts rather than reading from Room directly. This preserves the architecture boundary between UI and persistence and keeps the future sync implementation behind the repository layer.

### Sync metadata before a sync engine

Every field note and site carries `SyncMetadata` (state, last sync time, and structured failure). Repositories set it on every write, so the local database already knows what needs to be created, updated, or deleted remotely before any sync code exists. The transition rules live on `SyncMetadata` itself and are unit tested, so a future sync worker only has to call `markSynced` or `markFailed`.

### Soft deletes

Deleting a record that has already synced marks it `PENDING_DELETE` instead of removing the row, so the delete can still be sent to a server. Records that were never synced are deleted outright. UI-facing DAO queries hide deleted rows through one shared `NOT_DELETED` SQL fragment, which mirrors `SyncMetadata.isDeleted`; `...ForSync` queries still see them. Because a soft-deleted site keeps its row, the site repository unassigns the site's notes explicitly rather than relying on the `ON DELETE SET NULL` foreign key.

### Transactional repository writes

Saves and deletes read the current sync state and then write. Each of these paths runs inside a Room transaction so a future sync worker cannot change the row between the read and the write. Transactions do not cover network calls, so the sync worker will still need its own check before marking a record synced.

### Type-safe navigation and route-scoped ViewModels

Destinations are `@Serializable` classes under `GroundWorkRoute`, so arguments are checked at compile time and required arguments, such as Site Detail's site ID, cannot be missing. Route composables read their typed route from the back stack and pass it to the ViewModel through a Hilt assisted factory. ViewModels therefore take a plain route object, which keeps them free of `SavedStateHandle` key lookups and lets local unit tests construct them directly.

### Typed UI errors

ViewModels expose per-screen error enums (for example `SiteEditorError.SAVE_FAILED`) instead of display strings. Each error carries its string resource, and screens resolve the text with `stringResource`. This keeps user-facing text in string resources, ready for translation, and lets tests assert on error values rather than English sentences.

### Injected time and IDs

Editor ViewModels receive a `java.time.Clock` and an `IdGenerator` from Hilt instead of calling `System.currentTimeMillis()` and `UUID.randomUUID()` directly. Tests use a fixed clock and a predictable ID, which matters for upcoming sync and conflict logic that depends on timestamps.

### ViewModel-level search and filtering

Search and filtering currently run in list ViewModels over observed local data. This keeps the first implementation simple, testable, and easy to combine with UI state. DAO-backed search or Room FTS can be introduced later if larger datasets make in-memory filtering inappropriate.

### Testable local foundation before sync

Domain, migration, DAO, repository, ViewModel, UI state, and Compose tests were in place before the sync simulation begins, so sync can be added against a well-covered local layer.

## What Works Now

- Debug and R8-minified release builds succeed with no compiler warnings or lint issues
- GitHub Actions runs unit tests, a debug build, and instrumented tests on an emulator for every push and pull request
- Project structure separates domain, data, dependency injection, navigation, and feature UI concerns
- Room is configured with schema export enabled, and every migration (1→2 through 4→5) is tested
- Field notes can be persisted through the Room DAO and repository layer
- Sites can be persisted through the Room DAO and repository layer
- Repository implementations map between Room entities and domain models
- Hilt provides database, DAO, repository, clock, and ID generator dependencies
- Every create, update, and delete is recorded as a pending sync change, and deletes of synced records are soft until sync completes
- `FieldNotesListViewModel` observes the field note repository and exposes list UI state
- `SitesListViewModel` observes the site repository and exposes list UI state
- `FieldNotesListScreen` renders loading, empty, error, content, and no-match states
- `SitesListScreen` renders loading, empty, error, content, and no-match states
- Field notes can be searched by title, body, and associated site name
- Field notes can be filtered by status
- Field notes can be assigned, reassigned, or returned to an unassigned state
- New field notes created from Site Detail start with that site selected
- Site Detail shows notes belonging to the selected site
- Deleting a site preserves its field notes as unassigned and marks them for sync
- Sites can be created, edited, deleted, listed, searched, and filtered locally
- Sites require a name; names are trimmed on save, and a hint appears when the name is cleared
- Sites support status (Active/Archived) and priority (Low/Normal/High)

Site priority represents long-term operational importance. Urgency is intentionally not modeled on Site because urgency usually belongs to time-sensitive issues, tasks, or incidents associated with a site.

- Bottom navigation switches between Field Notes and Sites
- Search and filters can be combined
- No-match states are shown when active search/filter criteria return no results
- `FieldNoteEditorScreen` supports creating and editing field notes
- `SiteEditorScreen` supports creating and editing sites
- Field note cards can open existing notes for editing
- Site cards open Site Detail, where the site can be edited and its field notes managed
- New field notes and sites can be saved locally and shown in their lists
- Existing field notes and sites can be updated without duplicating records
- Existing field notes and sites can be deleted from editor screens
- Unsaved drafts can be discarded without creating or deleting persisted data
- Error messages come from string resources, selected by typed ViewModel errors
- Preview data reflects the field-operations product direction

## What Is Intentionally Not Built Yet

- Sync worker and fake remote data source (sync metadata is tracked, but nothing is sent yet)
- Sync status UI and manual retry
- Conflict resolution
- Attachments and evidence capture
- Maps/GPS
- Authentication
- Backend integration

## Testing Strategy

Tests are split by what they need. Logic that runs on the JVM is covered by fast local unit tests; anything that depends on SQLite, Room, or Compose rendering runs as instrumented tests on an emulator. ViewModel tests use in-memory fake repositories, a fixed `Clock`, and a predictable `IdGenerator`, so saved IDs and timestamps are asserted exactly.

### Local unit tests (`app/src/test`)

- **Domain:** `SyncMetadataTest` covers every sync state transition, including keeping unsynced creates as creates, retrying failed creates, rejecting updates to deleted records, and recording failures with their operation
- **Mappers:** `FieldNoteMapperTest`, `SiteMapperTest`, `SyncMetadataMapperTest`, and `PersistedEnumMapperTest` cover entity ↔ domain conversion, sync metadata round trips, nullable site links, and safe defaults for unknown persisted values
- **List ViewModels:** `FieldNotesListViewModelTest` and `SitesListViewModelTest` cover repository updates, search by title, body, description, location, and site name, status and priority filters, combined and cleared criteria, per-site note counts, and load errors
- **Editor ViewModels:** `FieldNoteEditorViewModelTest` and `SiteEditorViewModelTest` cover load, create, edit, delete, and discard flows, exact generated IDs and timestamps, site assignment and reassignment, site preselection from Site Detail, site name trimming and the required-name hint, and typed save, delete, and load errors
- **Site Detail:** `SiteDetailViewModelTest` covers loading a site and its notes, live updates, the site being deleted while open, and site and note load errors
- **UI state models:** list, editor, and detail UI state tests cover derived flags such as `canSave`, `isBusy`, empty and no-match states, active criteria, and the required-name hint

### Instrumented tests (`app/src/androidTest`)

- **Migrations:** `GroundWorkDatabaseMigrationTest` runs every migration from version 1 to 5 against real schemas, including converting orphaned site links to unassigned and adding sync columns to existing rows
- **DAOs:** `FieldNoteDaoTest` and `SiteDaoTest` cover persistence, ordering, site-scoped and unassigned queries, upserts, deletes, and the foreign key that unassigns notes when a site row is removed
- **Repositories:** `OfflineFirstFieldNoteRepositoryTest` and `OfflineFirstSiteRepositoryTest` use in-memory Room databases to cover pending create/update/delete tracking, soft deletes and their visibility, rejecting saves of deleted records, and unassigning a deleted site's notes while leaving already-deleted notes untouched
- **Compose UI:** `FieldNoteEditorScreenTest` (associated-site selection and site-loading errors), `SiteEditorScreenTest` (required-name hint), `SiteCardTest` (card content and click), and `SitesSearchAndFiltersTest` (independent status and priority filters)

### Continuous integration

GitHub Actions runs on every push to `main` and every pull request. The first job runs `testDebugUnitTest` and `assembleDebug`; the second runs `connectedDebugAndroidTest` on an API 30 emulator on Ubuntu with KVM. A newer push to the same branch or pull request cancels the run in progress.

### Planned next tests

- Sync worker coverage: successful sync, failures and retry, and a local edit made while an upload is in flight
- Conflict-case coverage when v1.0 begins
- End-to-end navigation flows and screenshot tests for the existing previews

## Performance Notes

Performance work is intentionally limited while the app is still small. Current UI uses stable item keys in `LazyColumn` through domain IDs, and search/filtering currently runs in ViewModels over observed local data. This is acceptable for the current local dataset size; Room FTS or DAO-backed search can be introduced later if the dataset grows enough to justify it.

Future performance work may include:
- Larger local datasets
- Recomposition checks
- Search responsiveness
- Baseline Profile setup
- Macro-benchmark coverage for startup and list scrolling

## Roadmap

### v0.9.0 — Offline sync simulation
- Pending/failed/synced states (done)
- Soft deletes and transactional repository writes (done)
- Fake remote source
- Sync worker that processes pending changes
- Manual retry and sync status indicator

### v1.0 — Conflict handling
- Detect local/remote conflicts
- Conflict resolution UI
- Tests for conflict cases

### v1.1 — Attachments and evidence
- Add attachment metadata
- Local image URI handling
- Permission handling
- Storage tradeoffs

### v1.2 — Dashboard and polish
- Operational dashboard
- Sync summary
- Performance pass
- Baseline profile

## Tradeoffs

### Delaying sync

Sync is a major part of the long-term app direction, but it was intentionally delayed until the local source of truth, repository boundary, and UI state flow were correct. It is now being added in layers: sync metadata, soft deletes, and transactional writes first, so the sync worker, retries, and conflict handling can build on a local model that already knows what has changed.

### Sequencing field note/site association

Sites were implemented as their own local foundation before the relationship between sites and field notes was added in v0.8.0. This kept v0.7.0 focused on validating the Sites model, persistence, list/editor UI, navigation, and tests before adding cross-feature behavior.

### Avoiding premature modularization

The project starts with a simple feature-oriented package structure instead of a large multi-module setup. Additional modules or core packages will be introduced only when shared code or build boundaries justify them.
