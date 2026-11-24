# GroundWork testing guide

[README](../README.md) · [Architecture](architecture.md) · [Quick recall](quick-recall.md) · [Testing](testing.md) · [Roadmap](roadmap.md) · [History](history.md)

## Testing Strategy

Tests are split by what they need. Logic that runs on the JVM is covered by fast local unit tests; anything that depends on SQLite, Room, or Compose rendering runs as instrumented tests on an emulator. ViewModel tests use in-memory fake repositories, a fixed `Clock`, and a predictable `IdGenerator`, so saved IDs and timestamps are asserted exactly.

### Local unit tests (`app/src/test`)

- **Domain:** `SyncMetadataTest` covers every sync state transition, including keeping unsynced creates as creates, retrying failed creates, rejecting updates to deleted records, and recording failures with their operation
- **Mappers:** `FieldNoteMapperTest`, `SiteMapperTest`, `SyncMetadataMapperTest`, and `PersistedEnumMapperTest` cover entity ↔ domain conversion, sync metadata round trips, nullable site links, and safe defaults for unknown persisted values
- **List ViewModels:** `FieldNotesListViewModelTest` and `SitesListViewModelTest` cover repository updates, search by title, body, description, location, and site name, status and priority filters, combined and cleared criteria, per-site note counts, the Sites list's clock time and its refresh at each minute boundary without refiltering, and load errors
- **Editor ViewModels:** `FieldNoteEditorViewModelTest` and `SiteEditorViewModelTest` cover load, create, edit, delete, and discard flows, exact generated IDs and timestamps, site assignment and reassignment, site preselection from Site Detail, site name trimming and the required-name hint, unsaved input surviving process death (including a restored draft winning over the reloaded record), and typed save, delete, and load errors
- **Site Detail:** `SiteDetailViewModelTest` covers loading a site and its notes, the site name on note rows (including renames, with unchanged rows reused across other site edits), live updates, the site being deleted while open, and site and note load errors
- **UI state models:** list, editor, and detail UI state tests cover derived flags such as `canSave`, `isBusy`, empty and no-match states, active criteria, and the required-name hint
- **Shared helpers:** `SuspendRunCatchingTest` covers turning failures into results while rethrowing cancellation and leaving errors uncaught
- **Navigation:** `NavigatorTest` covers opening screens on the current tab, switching tabs without losing each tab's screens, returning to the start tab from another tab's root, and skipping a deleted site's detail screen
- **Formatting:** `AbsoluteDateTimeLabelTest` covers the shared date pattern, formatting in the formatter's zone and locale, and empty labels for unset timestamps; `MinuteTicksTest` covers emitting the clock time immediately and then on each minute boundary
- **Simulated backend:** `RemoteWireFormatTest` pins the JSON format with literal JSON (field names, ISO timestamps, lowercase enums, explicit null site links, ignored unknown fields, rejected malformed values); `RemoteMappersTest` covers domain → remote mapping; `FakeGroundWorkServerTest` covers revisions, idempotent retries and deletes, server-side validation and ordering rules, and simulated failures; `SyncRemoteDataSourceTest` covers mapping every outcome to success, transient failure, or rejection, and that cancellation is not swallowed

### Instrumented tests (`app/src/androidTest`)

- **Migrations:** `GroundWorkDatabaseMigrationTest` runs every migration from version 1 to 5 against real schemas, including converting orphaned site links to unassigned and adding sync columns to existing rows
- **DAOs:** `FieldNoteDaoTest` and `SiteDaoTest` cover persistence, ordering, site-scoped and unassigned queries, upserts, deletes, and the foreign key that unassigns notes when a site row is removed
- **Repositories:** `OfflineFirstFieldNoteRepositoryTest` and `OfflineFirstSiteRepositoryTest` use in-memory Room databases to cover pending create/update/delete tracking, soft deletes and their visibility, rejecting saves of deleted records, and unassigning a deleted site's notes while leaving already-deleted notes untouched
- **Compose UI:** `FieldNoteEditorScreenTest` (associated-site selection and site-loading errors), `SiteEditorScreenTest` (required-name hint), `SiteCardTest` (card content, click, and the relative updated time measured from the given time and refreshed when it changes), `FieldNoteItemCardTest` (updated time and site label rendered with the screen's shared formatter and locale), `AbsoluteDateTimeFormatterTest` (formatter follows the composition locale and system zone and is reused across recompositions), `SitesSearchAndFiltersTest` (independent status and priority filters), and `DropUnlessResumedTest` (a repeated navigation tap is dropped during and after the transition)

### Continuous integration

GitHub Actions runs on every push to `main` and every pull request. The first job runs `testDebugUnitTest` and `assembleDebug`; the second runs `connectedDebugAndroidTest` on an API 30 emulator on Ubuntu with KVM. A newer push to the same branch or pull request cancels the run in progress.

### Planned next tests

- Sync worker coverage: successful sync, failures and retry, and a local edit made while an upload is in flight
- Conflict-case coverage when v1.0 begins
- End-to-end navigation flows and screenshot tests for the existing previews
