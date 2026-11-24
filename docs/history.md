# GroundWork version history

[README](../README.md) · [Architecture](architecture.md) · [Quick recall](quick-recall.md) · [Testing](testing.md) · [Roadmap](roadmap.md) · [History](history.md)

Historical milestones and the work accumulated toward v0.9.0. Earlier entries retain their original implementation names; use the architecture and quick-recall guides for current code locations.

## Current Progress

The stable checkpoint tag is `v0.8.0`, representing the field-note/site relationship, site-aware creation and editing flows, Site Detail, and assigned/unassigned test coverage. The pre-release tag `v0.9.0-alpha` marks the completed sync groundwork described below, before the sync engine itself.

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

### v0.9.0 — Sync Simulation (in progress; groundwork tagged `v0.9.0-alpha`)

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

Simulated backend:
- Defined the backend's own JSON wire format (snake_case fields, ISO-8601 UTC timestamps, lowercase enums, no local sync metadata) with separate remote models, mapped from the domain
- Put the backend behind an HTTP-shaped `RemoteTransport` (method, path, JSON body in; status, JSON body out; `IOException` for network failures) so a real HTTP client can replace the fake later
- Added an in-memory `FakeGroundWorkServer` that stores records as JSON, numbers revisions per record, makes retries and deletes idempotent, enforces server-side rules (required fields, notes only for known sites, no deleting a site in use), and can simulate latency, network errors, server errors, and rejections
- Added `SyncRemoteDataSource`, which reports every call as success, transient failure (retry later), or rejection (give up), so the sync engine never handles JSON or status codes

Architecture and quality:
- Moved navigation to type-safe `@Serializable` routes; Site Detail now always has a site ID
- Migrated from Navigation Compose to Navigation 3: each bottom-bar tab keeps its own back stack in `NavigationState`, changed only through `Navigator`, and the stacks survive process death
- Stopped editor ViewModels from reporting coroutine cancellation as a failed save, delete, or load: failable work goes through `suspendRunCatching` in `core/common`, which rethrows `CancellationException` and doesn't catch `Error`s
- Kept unsaved editor input across process death through each editor's `SavedStateHandle`; when editing an existing record, a restored draft wins over the record reloaded from the database
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
- Sync worker that processes pending changes and records success or failure
- Guard against losing a local edit made while an upload is in flight
- Manual retry and a sync status indicator in the UI
