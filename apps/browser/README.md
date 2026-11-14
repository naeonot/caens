# CAENS Browser

Status: early Android WebView prototype; not build-tested yet.

## UI direction
- Warm off-white / ivory browser canvas with white rounded cards.
- Soft gradient header.
- Theme picker: Ocean Blue, Coral Red, Lavender, Mint Green and Warm Amber.
- World mode with a globe icon; ON by default.
- Home screen shortcuts, address/search bar and basic navigation controls.

## World mode
- ON: normal public HTTP/HTTPS websites are allowed.
- OFF: public destinations are blocked in this browser; navigation is reserved for configured CAENS hostnames.
- The toggle is a client-side browsing preference, not a security boundary.

## Current limitations
- CAENS gateway hostnames are placeholders (`caens.local`, `caens.internal`).
- Bookmarks and History tabs are visual placeholders for a later iteration.
- Theme selection currently applies the selected palette to the header; broader theme persistence and per-screen theming are future work.
- The private gateway, identity provider, device enrollment and server-side authorization are not configured.
- No APK has been generated or tested yet.

## Build
Open `android/` in Android Studio with JDK 17 and Android SDK 35 or compatible tooling.

Package: `com.caens.browser`
