# GlassOS v3 — Liquid Glass visual preview

Android 11 launcher project for HY260 Pro. Source only; APK must be built and tested.

## What changed
- Approved misty forest wallpaper as a separate 1920×1080 JPEG.
- Five approved transparent glass tile graphics; each is a clickable and remote-focusable view.
- Dock icon image assets (installed Android app icons take priority where available).
- Removed GlassOS / HY260 PRO text from home screen and removed preview subtitle.
- Settings tile goes to Android system settings instead of GlassOS custom settings.
- Existing app launch and HOME registration preserved.

## Known limitations
- HDMI switching still requires discovering OEM-specific activity; it is not implemented. No fake claim of working HDMI.
- USB uses Android document picker, not necessarily OEM USB screen.
- App list still uses an Android dialog (not yet replaced with the approved grid).
- Screensaver not implemented in this version.
- Graphics are pre-rendered; this is not real-time Apple Liquid Glass rendering.
- APK compilation and projector behavior have not been verified for this source revision.

## Build
GitHub Actions workflow: `.github/workflows/android.yml`. Upload project contents at repository root, run workflow, then download `GlassOS-APK` artifact if build succeeds.
