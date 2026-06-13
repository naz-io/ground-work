# GroundWork

GroundWork is an offline-first Android app for field operations teams working in low-connectivity environments.

The long-term product direction includes job/site management, field notes, reports, local sync visibility, and conflict-aware offline workflows. The current implementation intentionally starts smaller: a local-first field notes foundation.

## Project Goal

The goal of this project is not to build another generic notes app. The goal is to demonstrate senior Android engineering judgment through a small but realistic product surface: local-first persistence, predictable UI state, clear architecture boundaries, and a design that can later support sync and conflict resolution without rewriting the app.

The first version focuses on getting the local foundation right: creating, editing, deleting, listing, and searching field notes using a clean Compose UI, a Room-backed data layer, ViewModel-managed state, and a repository contract between the domain and data layers.

Future versions will extend this foundation with job/site management, sync simulation, retry handling, conflict resolution, attachments, and performance/testing work. These are intentionally delayed until the local model and state flow are stable.

## Seniority Signals

This project is designed to showcase:

- Offline-first architecture with local persistence as the foundation
- Clear separation between domain, data, dependency injection, navigation, and feature UI layers
- Explicit UI state modeling with ViewModels and Compose
- Repository-driven data access instead of direct database access from UI
- Route/screen separation for previewable, stateless Compose UI
- Scope control: sync, conflict resolution, job sites, and attachments are delayed until the local foundation is stable
- A roadmap that grows complexity intentionally instead of adding features randomly
- Local unit tests for mappers, DAOs, repositories, and ViewModels

## Current Progress

Git tags are created only for stable checkpoints. The current checkpoint is `v0.6.1`, representing the local field notes foundation with create/edit/delete, search/filtering, DAO tests, repository tests, ViewModel tests, and CI workflow.

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

### v0.7 — Job Sites
- Not implemented yet

### v0.8 — Sync Simulation
- Not implemented yet

### v0.9 — Conflict Handling
- Not implemented yet

## MVP Scope

### In scope for v1
- Create, edit, delete, list, and search field notes
- Persist field notes locally using Room
- Model UI state explicitly in ViewModels
- Keep composables mostly stateless
- Keep Room entities separate from domain models
- Build architecture that can support job sites and sync later

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

The app starts with a small domain model before introducing Room or UI implementation details. `FieldNote`, `FieldNoteId`, and `FieldNoteStatus` describe the core app concept independently of persistence, navigation, or Compose. The repository contract defines how the rest of the app will access field notes without depending directly on the database layer.

### Room as future source of truth

Even before sync exists, Room is treated as the durable source for app data. The UI reads app state through repositories and ViewModels rather than directly from the database.

### Field notes before job sites

Field notes are the smallest useful slice of the larger GroundWork concept. They allow the project to demonstrate local persistence, editing flows, list/detail UI, search, and state handling without prematurely introducing job/site modeling, backend sync, GPS, maps, or media attachments.

The app is structured so field notes can later belong to job sites and participate in sync workflows.

### Route and screen separation

The first Compose screen separates the ViewModel-connected route from the stateless screen. `FieldNotesRoute` collects `FieldNotesViewModel.uiState`, while `FieldNotesListScreen` only receives state and renders UI. This keeps the screen previewable, easier to test, and independent of Hilt.

### Repository-driven UI state

`FieldNotesViewModel` observes `FieldNoteRepository` rather than reading from Room directly. This preserves the architecture boundary between UI and persistence and keeps the future sync implementation behind the repository contract.

### ViewModel-level search and filtering

Search and status filtering currently run in the list ViewModel over the observed local field notes. This keeps the first search/filter implementation simple, testable, and easy to combine with UI state. DAO-backed search or Room FTS can be introduced later if larger datasets make in-memory filtering inappropriate.

### Testable local foundation before sync

DAO, repository, and ViewModel tests are added before sync simulation.

## What Works Now

- Android project builds successfully
- GitHub repository is connected
- Project structure separates domain, data, dependency injection, navigation, and feature UI concerns
- Room is configured with schema export enabled
- Field notes can be persisted through the Room DAO and repository layer
- Repository implementation maps between Room entities and domain models
- Hilt provides the database, DAO, and repository dependencies
- `FieldNotesViewModel` observes the repository and exposes `StateFlow<FieldNotesUiState>`
- `FieldNotesListScreen` renders loading, empty, error, and content states
- Field notes can be searched by title and body
- Field notes can be filtered by status
- Search and status filters can be combined
- No-match states are shown when active search/filter criteria return no results
- `FieldNoteEditorScreen` supports creating and editing field notes
- Field note cards can open existing notes for editing
- New field notes can be saved locally and shown in the list
- Existing field notes can be updated without duplicating the note
- Existing field notes can be deleted from the editor
- Unsaved drafts can be discarded without creating or deleting persisted field notes
- Preview data reflects the field-operations product direction
- Mapper tests cover `FieldNoteEntity` ↔ `FieldNote` conversion
- DAO tests cover local Room persistence, search, deletion, and ordering behavior
- `FieldNotesListViewModel` tests cover search, filtering, combined criteria, and error state behavior

## What Is Intentionally Not Built Yet

- Repository tests using an in-memory Room database
- Editor ViewModel tests for create, edit, delete, and discard flows
- Job/site management
- Real sync
- Sync status UI
- Conflict resolution
- Attachments and evidence capture
- Maps/GPS
- Authentication
- Backend integration

## Testing Strategy

Testing currently covers the local field notes foundation at the mapper, DAO, repository, and ViewModel layers. The next testing pass should cover repository behavior with an in-memory Room database and editor ViewModel state transitions for create, edit, delete, and discard flows.

Implemented tests:
- Mapper tests for `FieldNoteEntity` ↔ `FieldNote`
- DAO tests using an in-memory Room database
- `FieldNotesListViewModel` tests for initial data, search, status filtering, combined search/filter criteria, body search, and error state

Planned next tests:
- Repository tests using an in-memory Room database
- `FieldNoteEditorViewModel` tests for create, edit, delete, and discard flows
- Basic Compose UI tests after core ViewModel/repository behavior is covered

GitHub Actions will be added after these checks pass locally.

## Performance Notes

Performance work is intentionally limited while the app is still small. Current UI uses stable item keys in `LazyColumn` through `FieldNoteId`, and search/filtering currently runs in the ViewModel over the observed local field notes. This is acceptable for the current local dataset size; Room FTS or DAO-backed search can be introduced later if the dataset grows enough to justify it.

Future performance work may include:
- Larger local datasets
- Recomposition checks
- Search responsiveness
- Baseline Profile setup
- Macro-benchmark coverage for startup and list scrolling

## Roadmap

### v0.6 — Repository tests and editor ViewModel tests
- Add repository tests using an in-memory Room database
- Add editor ViewModel tests for create, edit, delete, and discard flows
- Add GitHub Actions after local checks are stable

### v0.7 — Job sites
- Add `JobSite` model
- Associate field notes with job sites
- Job/site list and detail screen

### v0.8 — Offline sync simulation
- Sync queue
- Pending/failed/synced states
- Manual retry
- Fake remote source

### v0.9 — Conflict handling
- Detect local/remote conflicts
- Conflict resolution UI
- Tests for conflict cases

### v1.0 — Attachments and evidence
- Add attachment metadata
- Local image URI handling
- Permission handling
- Storage tradeoffs

### v1.1 — Dashboard and polish
- Operational dashboard
- Sync summary
- Performance pass
- Baseline profile

## Tradeoffs

### Delaying sync

Sync is a major part of the long-term app direction, but it is intentionally delayed. The first priority is to make the local source of truth, repository boundary, and UI state flow correct before introducing remote state, retries, or conflict handling.

### Delaying job sites

Job sites are part of the final product direction, but field notes are implemented first because they are the smallest useful slice of the app. This avoids building a large domain model before the basic local workflow is proven.

### Avoiding premature modularization

The project starts with a simple feature-oriented package structure instead of a large multi-module setup. Additional modules or core packages will be introduced only when shared code or build boundaries justify them.