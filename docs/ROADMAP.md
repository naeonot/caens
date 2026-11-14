# CAENS development roadmap

This is a staged plan, not a promise of completion dates.

## Stage 0 — Foundation
- [x] Create monorepo structure and module ownership rules.
- [x] Add first Android browser source scaffold.
- [ ] Confirm product scope and core app boundaries.
- [ ] Set up basic CI and code-quality checks.

## Stage 1 — Browser prototype
- [x] Android WebView shell.
- [x] Address/search input and basic navigation controls.
- [x] Internet Mode toggle.
- [ ] Build and test on a physical Android device.
- [ ] Add bookmarks, tabs and downloads only after the basic shell is stable.

## Stage 2 — CAENS access foundation
- [ ] Define CAENS identity and account model.
- [ ] Define device enrollment and revocation.
- [ ] Build private gateway and service discovery.
- [ ] Enforce authorization on the server, not only in the browser.
- [ ] Define trusted CAENS hostnames and TLS strategy.

## Stage 3 — First ecosystem services
- [ ] Cloud
- [ ] Chat
- [ ] BRESÉ (AI assistant)
- [ ] Mail
- [ ] Vault

## Stage 4 — Reliability and security
- [ ] Backups and restore drills.
- [ ] Monitoring and audit events.
- [ ] Threat modelling and security review.
- [ ] Remote access and multi-user permission testing.

## Current rule

Do not build every planned service at once. Finish and test one foundation at a time. Planned folders are organizational boundaries, not claims that those services already exist.
