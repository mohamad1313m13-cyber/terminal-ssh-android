# Changelog

All notable changes to this project are documented in this file.

## [0.1.0] - 2026-08-13

### Added
- Password-authenticated SSH shell over mwiede JSch.
- Real VT100/ANSI terminal (xterm-256color PTY) via ConnectBot termlib/libvterm.
- Explicit TOFU host-key fingerprint approval and strict changed-host-key rejection.
- AndroidKeyStore + AES-GCM vault for sensitive temporary values.
- Clipboard integration and PTY resize.
- `FLAG_SECURE`, cleartext disabled, backup/device-transfer exclusions.
- CI: unit tests, lint, debug APK, unsigned release APK/AAB with artifact upload.
- Persian market listing and privacy policy under `store/`.

### Changed
- Gradle wrapper added and pinned to Gradle 8.13.
- Kotlin compiler options migrated to the `compilerOptions` DSL.
- R8/ProGuard: `dontwarn` rules for JSch optional dependencies.

### Security
- Host-key verification is enforced; `StrictHostKeyChecking=no` is never used.
- Passwords are not converted to immutable `String` where avoidable.
