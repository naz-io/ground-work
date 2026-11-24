# GroundWork scope and roadmap

[README](../README.md) · [Architecture](architecture.md) · [Quick recall](quick-recall.md) · [Testing](testing.md) · [Roadmap](roadmap.md) · [History](history.md)

The sync metadata and simulated backend are implemented. The worker connecting local records to that backend, retry UI, and conflict handling remain planned.

## MVP Scope

### In scope for v1

- Create, edit, delete, list, and search field notes
- Persist field notes locally using Room
- Manage sites locally using Room
- Model UI state explicitly in ViewModels
- Keep composables mostly stateless
- Keep Room entities separate from domain models
- Associate field notes with sites after both local foundations are stable
- Track local changes with sync metadata and sync them against a simulated remote source
- Detect and resolve conflicts between local and simulated remote changes

### Out of scope for v1

- Real backend
- Authentication
- Attachments
- Maps
- GPS
- Push notifications
- Multi-user collaboration
- Sync with a real server (sync and conflict handling run against a simulated remote source)

## Roadmap

### v0.9.0 — Offline sync simulation

- Pending/failed/synced states (done)
- Soft deletes and transactional repository writes (done)
- Fake remote source (done)
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

## Performance Notes

Performance work is intentionally limited while the app is still small. Current UI uses stable item keys in `LazyColumn` through domain IDs, and search/filtering currently runs in ViewModels over observed local data. This is acceptable for the current local dataset size; Room FTS or DAO-backed search can be introduced later if the dataset grows enough to justify it.

Future performance work may include:
- Larger local datasets
- Recomposition checks
- Search responsiveness
- Baseline Profile setup
- Macro-benchmark coverage for startup and list scrolling
