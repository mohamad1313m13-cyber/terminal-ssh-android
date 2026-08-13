# Terminal SSH 0.1.0

A minimal security-focused Android SSH terminal using mwiede JSch and ConnectBot termlib/libvterm.

## Release scope
- Password-authenticated SSH shell.
- Real VT100/ANSI terminal with xterm-256color PTY.
- Explicit TOFU fingerprint approval and strict changed-host-key rejection.
- AndroidKeyStore + AES-GCM vault for sensitive temporary values.
- Clipboard integration and PTY resize.
- `FLAG_SECURE`, cleartext disabled, backup/device-transfer exclusions.
- Market signing kept outside source control.

## Verification
Run:

```sh
python3 scripts/source_audit.py
python3 scripts/market_release_gate.py
./scripts/verify_jvm.sh
```

A machine with Android SDK 36, Gradle 8.13 and JDK 17 can run the full binary gate with `scripts/release_local.sh` after the four signing environment variables documented in `store/RELEASE_SIGNING.md` are set.

GitHub Actions contains both an unsigned verification build and a tag-only signed release job.
