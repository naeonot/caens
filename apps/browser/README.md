# CAENS Browser

Status: early prototype source.

CAENS Browser is intended to be the controlled entry point to CAENS services. The current Android implementation uses the Android System WebView; it is not a new browser engine.

## Current features
- URL and search input.
- Back, forward and reload controls.
- Internet Mode switch.
- Internet Mode ON: allows normal public HTTP/HTTPS websites.
- Internet Mode OFF: blocks public destinations in the app and reserves access for configured CAENS hosts.

## Important limitation
The CAENS private gateway and trusted hostnames do not exist yet. Therefore CAENS-only mode is a **local client-side restriction for testing**, not production security. A determined user can modify the client or use another browser. Real exclusivity requires server-side identity, device credentials and network authorization.

## Build
Open `android/` in Android Studio. Use JDK 17 and Android SDK 35 or a newer compatible SDK. Let Android Studio install the matching Gradle tooling if prompted.

Package: `com.caens.browser`

## Next
1. Build and install the app on Android.
2. Test Internet Mode ON/OFF.
3. Configure a real CAENS gateway hostname before enabling private-service access.
4. Add device enrollment and server-side authorization before calling it secure.
