# GlassOS Liquid Glass v2 — Android launcher source

**Status: SOURCE PROJECT, NOT APK. NOT COMPILED OR TESTED ON HY260 PRO.**

Android 11 compatible design target, 1920x1080 landscape, low-memory projector (~1 GB RAM).

## Features implemented in source
- Full-screen scenic mountain wallpaper asset (original procedural artwork).
- Translucent glass-like panels, clock, Russian date, upper tiles and nine-icon dock.
- Actual installed app icons loaded from Android PackageManager when available.
- TV remote DPAD focus, click, long-press context menu, animated focus.
- Launch installed apps, browse installed apps, customize dock slots, swap slots, restore defaults; settings saved on device.
- Settings / quick access panel opens Android system settings (when permitted).
- Android HOME intent registration: device may offer GlassOS as a home app, depending on OEM firmware.
- Return to factory interface via exit; factory firmware remains untouched.
- Netflix / YouTube / Play Store shortcuts where installed; no Disney+.

## Known limitations (must not conceal)
- This is NOT an exact implementation of Apple Liquid Glass. It uses lightweight translucent gradients, no GPU backdrop blur.
- HDMI requires identification of OEM activity: safe informational prompt currently, not a working input switch.
- Some OEM projectors ignore third-party HOME apps; automatic replacement is NOT guaranteed.
- USB tile currently opens a system document picker rather than switching input; availability depends on firmware.
- Weather requires an API and location and is not implemented.
- APK build and hardware validation are still required.
- This project uses targetSdk 28 for compatibility with legacy Android 11 launcher package discovery. Production distribution will require modernization.

## Build locally
Open folder in Android Studio with JDK 17 and Android SDK Platform 35. Gradle sync and Build > Build APK(s). APK output: app/build/outputs/apk/debug/app-debug.apk.

## Build without installing Android Studio
Upload the source contents to a private GitHub repository, enable Actions, open Actions > Build GlassOS APK > Run workflow. After a successful run download artifact GlassOS-debug-APK. **This requires a GitHub account, internet access, and a successful cloud build; it is not automatic from this ZIP.** The debug APK is for testing, not distribution.

## Installation and rollback
Install APK using a trusted file manager / USB drive. Open GlassOS from installed apps first. Only if the firmware offers a Home app chooser should you optionally set it as default. Keep access to factory launcher and Android settings. Never use local firmware update for this APK. Do not factory reset.
