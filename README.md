# CAENS

CAENS is a private digital ecosystem project. This repository is the **system source of truth**: architecture, applications, shared platform components, infrastructure, security, and operational documentation live here.

## Current scope

- Establish a maintainable monorepo structure before the system grows.
- Build the first application: **CAENS Browser**.
- Browser prototype includes an **Internet Mode** switch for public web browsing.
- CAENS-only mode is a client-side prototype until the CAENS gateway, identity service, and device authorization are implemented.

## Repository map

```text
caens/
├── apps/                  # User-facing applications
│   └── browser/           # CAENS Browser
├── platform/              # Shared identity, API, data and runtime foundations
├── infrastructure/       # Deployment, networking, observability and operations
├── security/              # Threat model, policies and security design
├── docs/                  # Product, architecture and engineering documentation
├── tools/                 # Developer utilities and scripts
└── .github/               # Repository templates and CI workflows
```

See [docs/REPOSITORY-STRUCTURE.md](docs/REPOSITORY-STRUCTURE.md) for folder ownership and rules, and [docs/ROADMAP.md](docs/ROADMAP.md) for staged development.

## Development status

| Area | Status |
|---|---|
| Repository architecture | Initial structure |
| Browser Android app | Prototype source scaffold |
| CAENS identity / device trust | Not implemented |
| Private network gateway | Not implemented |
| Other core apps | Planned, not scaffolded yet |

## Important security boundary

A custom browser alone cannot make a service private. Browser checks and client-side toggles can be bypassed. Production CAENS services must enforce authentication, device authorization, network policy, and per-service permissions on the server side.

## Naming

The repository is named `caens`. The parent ecosystem name can still be changed later; keep product identifiers and package names centralized and avoid scattering brand strings.
