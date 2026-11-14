# Decision 0001: Use a monorepo

- Status: Accepted
- Date: 2026-11-15

## Context
CAENS is expected to contain several connected applications and shared infrastructure. Separate repositories immediately would make shared standards and system-wide documentation harder to maintain.

## Decision
Use one repository with clear top-level boundaries: apps, platform, infrastructure, security, docs, tools and GitHub configuration.

## Consequences
- One place for system architecture and development standards.
- App teams/modules remain separated by folder ownership.
- CI can later build only the modules affected by a change.
- Repository discipline is required to avoid mixing app code with platform services.
