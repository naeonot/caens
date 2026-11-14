# CAENS

CAENS is a private digital ecosystem project. This repository is the **system source of truth**: architecture, applications, shared platform components, infrastructure, security, and operational documentation live here.

## Current scope

- Establish a maintainable monorepo structure before the system grows.
- Build the first application: **CAENS Browser**.
- Browser prototype includes a **World** switch for public web browsing.
- CAENS-only mode is a client-side prototype until the CAENS gateway, identity service, and device authorization are implemented.

## Repository map

```text
caens/
├── index.html              # Website entry point (GitHub Pages)
├── web/                    # All website code and build configuration
│   ├── src/                # React, TypeScript, CSS and components
│   ├── public/              # Static website assets
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
├── apps/                   # User-facing native applications
│   └── browser/android/    # CAENS Browser Android app
├── platform/               # Shared identity, API, data and runtime foundations
├── infrastructure/         # Deployment, networking, observability and operations
├── security/               # Threat model, policies and security design
├── docs/                   # Product, architecture and engineering documentation
├── tools/                  # Developer utilities and scripts
└── .github/                # Repository templates and CI workflows
```

The website source is kept in `web/`, while the root `index.html` is its entry point. The Pages workflow builds the site from the root entry and publishes the generated `web/dist/` output.

See [docs/REPOSITORY-STRUCTURE.md](docs/REPOSITORY-STRUCTURE.md) for folder ownership and rules, and [docs/ROADMAP.md](docs/ROADMAP.md) for staged development.

## Development status

| Area | Status |
|---|---|
| Repository architecture | Initial structure |
| Browser web preview | React + TypeScript + Vite scaffold |
| Browser Android app | Prototype source scaffold |
| CAENS identity / device trust | Not implemented |
| Private network gateway | Not implemented |
| Other core apps | Planned, not scaffolded yet |

## Important security boundary

A custom browser alone cannot make a service private. Browser checks and client-side toggles can be bypassed. Production CAENS services must enforce authentication, device authorization, network policy, and per-service permissions on the server side.
