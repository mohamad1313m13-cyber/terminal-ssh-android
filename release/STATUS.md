# Release status — 0.1.0

Source/configuration gates are complete for `app.terminalssh.secure`: security audit, market metadata audit, known-host policy smoke tests, AES-GCM vault smoke tests, XML validation, signing externalization, privacy/listing material, and CI release workflow.

The binary gate is intentionally not marked complete in this sandbox because Android SDK/Gradle executable downloads are blocked and no publisher keystore is available. A market APK/AAB must only be called release-ready after `testReleaseUnitTest`, `lintRelease`, `assembleRelease`, `bundleRelease`, APK/AAB signature verification, and device/emulator SSH validation pass with the publisher signing identity.
