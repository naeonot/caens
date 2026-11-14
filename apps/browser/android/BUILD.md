# CAENS Browser Android build

This directory contains the Kotlin Android WebView prototype.

Build a debug APK from this directory with:

```bash
gradle --no-daemon assembleDebug
```

The GitHub Actions workflow `.github/workflows/android-apk.yml` builds and uploads the debug APK as the `caens-browser-debug-apk` workflow artifact.
