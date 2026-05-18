# GroundWork
GroundWork is an offline-first field task and notes app built to demonstrate pragmatic Android architecture, resilient local state, and sync-ready design.

## Project Goal
GroundWork is an offline-first Android app for field tasks and notes.
The goal of this project is not to build another generic notes app. The goal is to demonstrate senior Android engineering judgment through a small but realistic product surface: local-first persistence, predictable UI state, clear architecture boundaries, and a design that can support sync and conflict resolution later without rewriting the app.
The first version focuses on getting the local foundation right: creating, editing, deleting, listing, and searching notes using a clean Compose UI, a Room-backed data layer, ViewModel-managed state, and a repository contract between the domain and data layers.
Future versions will extend this foundation with sync simulation, retry handling, conflict resolution, and performance/testing work. These are intentionally delayed until the local model and state flow are stable.

## Seniority Signals
This project is designed to showcase:
- Offline-first architecture with local persistence as the foundation
- Clear separation between domain, data, navigation, and feature UI layers
- Explicit UI state modeling with ViewModels and Compose
- Repository-driven data access instead of direct database access from UI
- Scope control: sync, conflict resolution, and attachments are delayed until the local foundation is stable
- A roadmap that grows complexity intentionally instead of adding features randomly

## Current Version
### v0.1 — Project Setup
- Created Android project
### v0.2 — Local Notes Foundation
- Not implemented yet
### v0.3 — Search and Filtering
- Not implemented yet
### v0.4 — Sync Simulation
- Not implemented yet
### v0.5 — Conflict Handling
- Not implemented yet

## MVP Scope
### In scope
- Create, edit, delete, and search notes
- Persist notes locally using Room
- Model UI state explicitly in ViewModels
- Keep composables mostly stateless
- Build architecture that can support sync later

### Out of scope for v1
- Real backend
- Authentication
- Attachments
- Maps
- Push notifications
- Multi-user collaboration

## Architecture Decisions
### Local-first foundation
The app starts with local persistence before any remote sync. This keeps the first version focused on reliable state and data flow instead of prematurely introducing backend complexity.
### Domain-first modeling
The app starts with a small domain model before introducing Room or UI implementation details. `Note`, `NoteId`, and `NoteStatus` describe the core app concept independently from persistence, navigation, or Compose. The repository contract defines how the rest of the app will access notes without depending directly on the database layer.
### Room as future source of truth
Even before sync exists, Room is treated as the durable source for app data. The UI reads app state through repositories and ViewModels rather than directly from the database.

## What Works Now
- Will be completed later
  
## What Is Intentionally Not Built Yet
- Will be completed later

## Testing Strategy
- Will be completed later

## Performance Notes
- Will be completed later

## Roadmap
- Will be completed later

## Tradeoffs
- Will be completed later
