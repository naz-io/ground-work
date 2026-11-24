# GroundWork quick recall

[README](../README.md) · [Architecture](architecture.md) · [Quick recall](quick-recall.md) · [Testing](testing.md) · [Roadmap](roadmap.md) · [History](history.md)

Use this before returning to the code or discussing the project. Each topic connects the implementation to its reason, a memorable edge case, and the tests that explain the behavior.

## Thirty-second recap

GroundWork is a local-first field notes and sites app. I built the Room foundation, optional site relationships, explicit Compose state, typed navigation, saved drafts, and tested sync metadata. A simulated JSON backend exists, but the worker connecting it to local changes is still next. The hardest rules are preserving pending operations, deleting safely, and keeping notes when their site is removed.

## Local capture and observable UI

**Built:** Field notes and sites have local CRUD, search, filters, and explicit loading/error/empty states. Routes collect state; screens receive state and callbacks.

**Why:** Room is durable storage and repository interfaces keep ViewModels independent of persistence. Search currently filters observed data in memory, which may need DAO-backed search at larger scale.

**Remember:** Editing must preserve an existing ID and creation timestamp. Fixed clocks and generated IDs make these assertions exact.

**Code:** [FieldNoteEditorViewModel](../app/src/main/java/com/nabadi/groundwork/feature/fieldnotes/editor/FieldNoteEditorViewModel.kt) · [FieldNotesListViewModel](../app/src/main/java/com/nabadi/groundwork/feature/fieldnotes/list/FieldNotesListViewModel.kt)

**Tests:** [Editor tests](../app/src/test/java/com/nabadi/groundwork/feature/fieldnotes/editor/FieldNoteEditorViewModelTest.kt) · [List tests](../app/src/test/java/com/nabadi/groundwork/feature/fieldnotes/list/FieldNotesListViewModelTest.kt)

**Remaining:** Measure performance with larger datasets before changing search or introducing modules.

## Optional site relationships

**Built:** Notes can be unassigned, assigned, or reassigned. Site Detail shows related notes and starts new notes with that site selected.

**Why:** Quick capture should not require creating a site first. The database relationship is nullable.

**Remember:** Soft-deleting a site does not trigger ON DELETE SET NULL. The repository explicitly unassigns visible notes and updates their timestamps and pending state in the same transaction; already-deleted notes stay untouched.

**Code:** [Site repository](../app/src/main/java/com/nabadi/groundwork/data/repository/OfflineFirstSiteRepository.kt) · [Migrations](../app/src/main/java/com/nabadi/groundwork/data/local/DatabaseMigrations.kt)

**Tests:** [Site repository tests](../app/src/androidTest/java/com/nabadi/groundwork/data/repository/OfflineFirstSiteRepositoryTest.kt) · [Migration tests](../app/src/androidTest/java/com/nabadi/groundwork/data/local/GroundWorkDatabaseMigrationTest.kt)

**Remaining:** The worker must respect remote relationship ordering when uploading unassignments and site deletes.

## Sync metadata and safe local writes

**Built:** Every local save/delete updates sync metadata. Previously synced deletes remain hidden pending records; pending and failed creates can be deleted outright.

**Why:** The future worker needs a durable record of what to send. Domain transitions centralize the rules, while repositories own database operations.

**Remember:** Editing a pending or failed create must stay a create. Editing a pending or failed delete is rejected. A Room transaction protects local read/write steps, but cannot guard a later network acknowledgment.

**Code:** [SyncMetadata](../app/src/main/java/com/nabadi/groundwork/domain/model/SyncMetadata.kt) · [Field note repository](../app/src/main/java/com/nabadi/groundwork/data/repository/OfflineFirstFieldNoteRepository.kt) · [Visibility predicates](../app/src/main/java/com/nabadi/groundwork/data/local/SyncQueries.kt)

**Tests:** [Transition tests](../app/src/test/java/com/nabadi/groundwork/domain/model/SyncMetadataTest.kt) · [Repository tests](../app/src/androidTest/java/com/nabadi/groundwork/data/repository/OfflineFirstFieldNoteRepositoryTest.kt)

**Remaining:** Build the worker, protect edits made during uploads, and implement remote-delete cleanup and retry UI.

## Simulated remote contract

**Built:** A separate JSON wire format, HTTP-shaped transport, fake server, and remote source with Success, Transient, and Rejected outcomes.

**Why:** Serialization, server validation, and failure classification should be exercised even before a real backend exists.

**Remember:** The fake stores data in memory and loses it when the process dies. Contract tests use literal JSON so a matching mistake in encoder and decoder is less likely to pass unnoticed.

**Code:** [Remote source](../app/src/main/java/com/nabadi/groundwork/data/remote/SyncRemoteDataSource.kt) · [Fake server](../app/src/main/java/com/nabadi/groundwork/data/remote/fake/FakeGroundWorkServer.kt)

**Tests:** [Wire-format tests](../app/src/test/java/com/nabadi/groundwork/data/remote/RemoteWireFormatTest.kt) · [Outcome tests](../app/src/test/java/com/nabadi/groundwork/data/remote/SyncRemoteDataSourceTest.kt)

**Remaining:** Connect it to pending local records; choose how simulated server state should behave across launches.

## Navigation and draft restoration

**Built:** Typed Navigation 3 routes, one back stack per tab, entry-scoped ViewModels, and editor drafts stored in SavedStateHandle.

**Why:** Tab switches should retain context, and process restoration should retain unsaved work. Navigator centralizes stack changes.

**Remember:** A restored editor draft must win over an existing record loaded from Room. Typed route injection supplies arguments; SavedStateHandle still stores draft fields.

**Code:** [Navigator](../app/src/main/java/com/nabadi/groundwork/navigation/Navigator.kt) · [NavigationState](../app/src/main/java/com/nabadi/groundwork/navigation/NavigationState.kt) · [Editor](../app/src/main/java/com/nabadi/groundwork/feature/fieldnotes/editor/FieldNoteEditorViewModel.kt)

**Tests:** [Navigation tests](../app/src/test/java/com/nabadi/groundwork/navigation/NavigatorTest.kt) · [Draft restoration tests](../app/src/test/java/com/nabadi/groundwork/feature/fieldnotes/editor/FieldNoteEditorViewModelTest.kt)

**Remaining:** Add end-to-end navigation coverage; unit-level restoration assertions are not a complete process-death UI test.

## Failure handling and verification

**Built:** Typed UI errors, a cancellation-aware suspendRunCatching helper, JVM tests, instrumented Room/Compose tests, and CI.

**Why:** Users need ordinary failures presented clearly, while coroutine cancellation must retain its control-flow meaning. Database behavior needs real SQLite coverage.

**Remember:** runCatching catches CancellationException. The shared helper rethrows it and catches Exception rather than Throwable, leaving Errors uncaught.

**Code:** [Failure helper](../app/src/main/java/com/nabadi/groundwork/core/common/SuspendRunCatching.kt)

**Tests:** [Helper tests](../app/src/test/java/com/nabadi/groundwork/core/common/SuspendRunCatchingTest.kt)

**Remaining:** Add worker and conflict tests when those features exist. Use the CI badge for current results; do not infer a fresh passing run from the existence of tests.

## Keeping this useful

When a feature changes, update its diagram or recall entry with the behavior, reason, edge case, and test link. Keep unfinished work explicitly marked. Put chronological details in [version history](history.md), so the README continues to describe the current app.
