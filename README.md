# GroundWork

GroundWork is an offline-first Android app for field operations teams working in low-connectivity environments.

The long-term product direction includes site management, field notes, reports, local sync visibility, and conflict-aware offline workflows. The current implementation intentionally starts smaller: a local-first foundation for field notes and sites, with the relationship between them planned as the next step.

## Project Goal

The goal of this project is not to build another generic notes app. The goal is to demonstrate senior Android engineering judgment through a small but realistic product surface: local-first persistence, predictable UI state, clear architecture boundaries, and a design that can later support sync and conflict resolution without rewriting the app.

The current version focuses on getting the local foundation right: creating, editing, deleting, listing, searching, and filtering field notes and sites using a clean Compose UI, a Room-backed data layer, ViewModel-managed state, and repository contracts between the domain and data layers.

Future versions will extend this foundation by associating field notes with sites, then adding sync simulation, retry handling, conflict resolution, attachments, and performance/testing work. These are intentionally delayed until the local model and state flow are stable.

## Seniority Signals

This project is designed to showcase:

- Offline-first architecture with local persistence as the foundation
- Clear separation between domain, data, dependency injection, navigation, and feature UI layers
- Explicit UI state modeling with ViewModels and Compose
- Repository-driven data access instead of direct database access from UI
- Route/screen separation for previewable, stateless Compose UI
- Scope control: sync, conflict resolution, field-note/site association, and attachments are delayed until the local foundations are stable
- A roadmap that grows complexity intentionally instead of adding features randomly
- Local and instrumented tests for mappers, DAOs, repositories, ViewModels, and UI state models

## Current Progress

Git tags are created only for stable checkpoints. The current checkpoint is `v0.7.0`, representing the local field notes and sites foundation with create/edit/delete flows, search/filtering, Room persistence, DAO tests, repository tests, ViewModel tests, UI state tests, and CI workflow.

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
- Planned next
- Associate field notes with sites using an optional `siteId`
- Keep field notes usable without a site for fast capture
- Add site-aware field note creation and editing flows
- Add list filtering or grouping by site where useful
- Add tests for assigned and unassigned field notes across DAO, repository, and ViewModel layers

### v0.9.0 — Sync Simulation
- Not implemented yet

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

The relationship between field notes and sites is intentionally delayed until both foundations are stable. Field notes will later be able to belong to sites while still supporting fast unassigned capture.

### Route and screen separation

Compose screens separate ViewModel-connected routes from stateless screen content. Routes collect ViewModel state, while screens receive state and callbacks. This keeps the screens previewable, easier to test, and independent of Hilt.

### Repository-driven UI state

ViewModels observe repository contracts rather than reading from Room directly. This preserves the architecture boundary between UI and persistence and keeps the future sync implementation behind the repository layer.

### ViewModel-level search and filtering

Search and filtering currently run in list ViewModels over observed local data. This keeps the first implementation simple, testable, and easy to combine with UI state. DAO-backed search or Room FTS can be introduced later if larger datasets make in-memory filtering inappropriate.

### Testable local foundation before sync

DAO, repository, ViewModel, and UI state tests are added before sync simulation.

## What Works Now

- Android project builds successfully
- GitHub repository is connected
- Project structure separates domain, data, dependency injection, navigation, and feature UI concerns
- Room is configured with schema export enabled
- Field notes can be persisted through the Room DAO and repository layer
- Sites can be persisted through the Room DAO and repository layer
- Repository implementations map between Room entities and domain models
- Hilt provides database, DAO, and repository dependencies
- `FieldNotesListViewModel` observes the field note repository and exposes list UI state
- `SitesListViewModel` observes the site repository and exposes list UI state
- `FieldNotesListScreen` renders loading, empty, error, content, and no-match states
- `SitesListScreen` renders loading, empty, error, content, and no-match states
- Field notes can be searched by title and body
- Field notes can be filtered by status
- Sites can be created, edited, deleted, listed, searched, and filtered locally
- Sites support status and priority modeling
- Bottom navigation switches between Field Notes and Sites
- Search and filters can be combined
- No-match states are shown when active search/filter criteria return no results
- `FieldNoteEditorScreen` supports creating and editing field notes
- `SiteEditorScreen` supports creating and editing sites
- Field note cards can open existing notes for editing
- Site cards can open existing sites for editing
- New field notes and sites can be saved locally and shown in their lists
- Existing field notes and sites can be updated without duplicating records
- Existing field notes and sites can be deleted from editor screens
- Unsaved drafts can be discarded without creating or deleting persisted data
- Preview data reflects the field-operations product direction
- Mapper tests cover `FieldNoteEntity` ↔ `FieldNote` conversion
- Mapper tests cover `SiteEntity` ↔ `Site` conversion
- DAO tests cover local Room persistence, deletion, ordering behavior, site-specific field note queries, and unassigned field note queries
- Repository tests cover field note and site persistence through in-memory Room databases
- `FieldNotesListViewModel` tests cover search, filtering, combined criteria, repository updates, clearing criteria, and error state behavior
- `FieldNoteEditorViewModel` tests cover create, edit, delete, discard, and error flows
- `SitesListViewModel` tests cover search, status filtering, priority filtering, combined criteria, repository updates, clearing criteria, and error state behavior
- `SiteEditorViewModel` tests cover create, edit, delete, discard, load, and error flows
- UI state tests cover list/editor state behavior for field notes and sites

## What Is Intentionally Not Built Yet

- Associating field notes with sites
- Site-aware field note creation and editing flows
- Real sync
- Sync status UI
- Conflict resolution
- Attachments and evidence capture
- Maps/GPS
- Authentication
- Backend integration

## Testing Strategy

Testing currently covers the local field notes and sites foundations at the mapper, DAO, repository, ViewModel, and UI state layers. Repository and DAO tests use an in-memory Room database where Android framework behavior matters, while ViewModel and UI state tests run as local unit tests.

Implemented tests:
- Mapper tests for `FieldNoteEntity` ↔ `FieldNote`
- Mapper tests for `SiteEntity` ↔ `Site`
- DAO tests using an in-memory Room database for field notes and sites
- Repository tests using an in-memory Room database for field notes and sites
- `FieldNotesListViewModel` tests for initial data, search, status filtering, combined search/filter criteria, body search, repository updates, clearing criteria, and error state
- `FieldNoteEditorViewModel` tests for create, edit, delete, discard, and error flows
- `SitesListViewModel` tests for initial data, search, status filtering, priority filtering, combined criteria, repository updates, clearing criteria, and error state
- `SiteEditorViewModel` tests for create, edit, delete, discard, load, and error flows
- UI state tests for field notes and sites list/editor state models

Planned next tests:
- Field note/site association tests across DAO, repository, and ViewModel layers
- Basic Compose UI tests only after the core local workflow becomes stable enough to justify emulator cost

GitHub Actions runs local unit tests and debug builds. Instrumented Android tests are maintained, but emulator reliability is handled separately from the core local unit-test gate.

## Performance Notes

Performance work is intentionally limited while the app is still small. Current UI uses stable item keys in `LazyColumn` through domain IDs, and search/filtering currently runs in ViewModels over observed local data. This is acceptable for the current local dataset size; Room FTS or DAO-backed search can be introduced later if the dataset grows enough to justify it.

Future performance work may include:
- Larger local datasets
- Recomposition checks
- Search responsiveness
- Baseline Profile setup
- Macro-benchmark coverage for startup and list scrolling

## Roadmap

### v0.8.0 — Field note and site relationship
- Associate field notes with sites using an optional `siteId`
- Keep unassigned field notes available for fast capture
- Add site-aware editor/list behavior
- Add DAO, repository, ViewModel, and UI state tests for assigned and unassigned notes

### v0.9.0 — Offline sync simulation
- Sync queue
- Pending/failed/synced states
- Manual retry
- Fake remote source

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

Sync is a major part of the long-term app direction, but it is intentionally delayed. The first priority is to make the local source of truth, repository boundary, and UI state flow correct before introducing remote state, retries, or conflict handling.

### Delaying field note/site association

Sites are now implemented as their own local foundation, but the relationship between sites and field notes is delayed until v0.8.0. This keeps v0.7.0 focused on validating the Sites model, persistence, list/editor UI, navigation, and tests before adding cross-feature behavior.

### Avoiding premature modularization

The project starts with a simple feature-oriented package structure instead of a large multi-module setup. Additional modules or core packages will be introduced only when shared code or build boundaries justify them.