# Repository structure and management rules

CAENS uses a **monorepo**: one repository, with clear boundaries between applications, shared platform services, infrastructure, security, and documentation.

## Top-level ownership

- `apps/`: independently buildable user-facing apps. Each app owns its UI, app-specific logic, tests, and build configuration.
- `platform/`: reusable backend capabilities shared by multiple apps. Do not put app-specific UI here.
- `infrastructure/`: environments, deployment definitions, networking, monitoring, backup and recovery.
- `security/`: threat models, security requirements, policies, review records and incident procedures.
- `docs/`: product decisions, architecture, roadmap, standards and onboarding.
- `tools/`: scripts for development, validation, release and repository maintenance.
- `.github/`: CI workflows, issue templates and pull-request templates.

## App structure convention

Each application should keep its platform-specific implementation under a named directory, for example:

```text
apps/<app-name>/
├── README.md
├── docs/
├── android/
├── web/             # only if a web client is actually needed
├── shared/          # app-local shared logic, not global platform code
└── tests/
```

Do not create empty speculative services or add dependencies before a real requirement exists.

## Engineering rules

1. Keep secrets, API keys, signing keys, private certificates and production credentials out of Git.
2. Add a README to each meaningful module describing purpose, status, how to run it and its owner/dependencies.
3. Prefer small, reviewable commits with one concern per commit.
4. Record important cross-system decisions in `docs/decisions/`.
5. Keep environment-specific configuration outside source code; commit safe examples only.
6. Never treat browser identity, User-Agent, a hidden URL, or a client-side toggle as an authorization boundary.
7. New services must document authentication, data ownership, backup, logging and failure behavior before production deployment.
8. Keep the root README current when modules or development status change.

## Current first application

The first implementation is `apps/browser/android`. It is an Android WebView-based browser shell, not a new browser engine. See its README for prototype limitations.
