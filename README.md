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

## Current Version

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

### v0.4 — Create and Edit Field Notes
- Not implemented yet

### v0.5 — Search and Filtering
- Not implemented yet

### v0.6 — Job Sites
- Not implemented yet

### v0.7 — Sync Simulation
- Not implemented yet

### v0.8 — Conflict Handling
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
- Preview data reflects the field-operations product direction

## What Is Intentionally Not Built Yet

- Creating, editing, and deleting field notes from the UI
- Search and filtering
- Job/site management
- Real sync
- Sync status UI
- Conflict resolution
- Attachments and evidence capture
- Maps/GPS
- Authentication
- Backend integration

## Testing Strategy

Testing has not been added yet. The first tests will be introduced after the create/edit flow is implemented so the app has meaningful behavior to verify.

Planned first tests:
- Mapper tests for `FieldNoteEntity` ↔ `FieldNote`
- Repository tests using an in-memory Room database
- ViewModel tests for loading, empty, content, and error states
- Basic UI tests after the create/edit flow exists

GitHub Actions will be added after these checks pass locally.

## Performance Notes

Performance work is intentionally delayed until the app has a real list/edit/search flow to measure. Current UI uses stable item keys in `LazyColumn` through `FieldNoteId`, which prepares the list for better item identity as the dataset grows.

Future performance work may include:
- Larger local datasets
- Recomposition checks
- Search responsiveness
- Baseline Profile setup
- Macro-benchmark coverage for startup and list scrolling

## Roadmap

### v0.1 — Project foundation
- GitHub repo
- README
- Package structure
- Room/KSP/Hilt setup

### v0.2 — Local field notes data layer
- FieldNote domain model
- Room entity/DAO/database
- Room schema export
- Mapper functions
- Repository implementation
- Hilt wiring

### v0.3 — Field notes state and list UI
- ViewModel state flow
- Route/screen separation
- Loading/empty/error/content states
- Basic field note cards

### v0.4 — Create and edit field notes
- Add field note editor UI
- Save new field notes
- Edit existing field notes
- Delete field notes
- Validate/fallback untitled notes

### v0.5 — Search and filtering
- Search field notes
- Filter by status
- Empty search state

### v0.6 — Job sites
- Add `JobSite` model
- Associate field notes with job sites
- Job/site list and detail screen

### v0.7 — Offline sync simulation
- Sync queue
- Pending/failed/synced states
- Manual retry
- Fake remote source

### v0.8 — Conflict handling
- Detect local/remote conflicts
- Conflict resolution UI
- Tests for conflict cases

### v0.9 — Attachments and evidence
- Add attachment metadata
- Local image URI handling
- Permission handling
- Storage tradeoffs

### v1.0 — Dashboard and polish
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
