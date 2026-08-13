# Terminal SSH

[![CI](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/actions/workflows/android-release.yml/badge.svg)](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/actions)
[![Release](https://img.shields.io/github/v/release/mohamad1313m13-cyber/terminal-ssh-android?sort=semver)](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases)
[![License](https://img.shields.io/github/license/mohamad1313m13-cyber/terminal-ssh-android)](LICENSE)

یک کلاینت SSH سبک و امن برای اندروید با ترمینال واقعی VT100/ANSI (xterm-256color)، احراز هویت با رمز عبور و اعتبارسنجی سختگیرانه Host Key.

A minimal security-focused Android SSH terminal built with [mwiede/JSch](https://github.com/mwiede/jsch) and [ConnectBot](https://github.com/connectbot) termlib/libvterm.

## دانلود / Download

نصب‌پذیرترین نسخه (APK دیباگ) را از بخش [Releases](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases/latest) دریافت کنید:

- **Debug APK** — قابل نصب مستقیم روی API 26+ برای تست (`TerminalSSH-0.1.0-debug.apk`)
- نسخه‌های امضاشده بازار فقط پس از پیکربندی کی‌استور ناشر ساخته می‌شوند (به `store/RELEASE_SIGNING.md` مراجعه کنید)

## ویژگی‌ها / Features

- اتصال SSH با رمز عبور / Password-authenticated SSH shell
- ترمینال واقعی VT100/ANSI با xterm-256color PTY
- تأیید صریح اثر انگشت در اولین اتصال (TOFU) و رد سختگیرانه تغییر Host Key
- Vault رمزنگاری‌شده با AndroidKeyStore + AES-GCM برای مقادیر حساس موقت
- یکپارچگی Clipboard و تغییر اندازه PTY
- `FLAG_SECURE`، غیرفعال‌بودن cleartext و حذف backup/انتقال داده
- امضای بازار خارج از کنترل نسخه نگهداری می‌شود

## امنیت / Security

- اعتبارسنجی Host Key اجباری است؛ هرگز `StrictHostKeyChecking=no` استفاده نمی‌شود
- رمزهای عبور به‌صورت String ماندگار تبدیل نمی‌شوند
- `android:allowBackup="false"` و `android:usesCleartextTraffic="false"`
- گیت امنیتی خودکار: `python3 scripts/source_audit.py`

## ساخت / Build

پیش‌نیازها: JDK 17، Android SDK 36، Gradle 8.13 (wrapper همراه پروژه است).

```sh
./gradlew testReleaseUnitTest    # تست‌های واحد
./gradlew lintRelease            # لینت
./gradlew assembleDebug          # APK دیباگ
./scripts/verify_jvm.sh          # گیت‌های JVM (لازم است kotlinc در PATH باشد)
```

خروجی‌ها در `app/build/outputs/` قرار می‌گیرند. GitHub Actions همین گیت‌ها را روی هر push/PR اجرا می‌کند و APK را به‌صورت artifact آپلود می‌کند.

## نسخه بازار / Market release

نشست و متن‌های فارسی برای کافه‌بازار و سیاست حریم خصوصی در پوشه `store/` آماده است. برای انتشار امضاشده:

1. کی‌استور ناشر را خارج از ریپو نگه دارید و چهار متغیر محیطی را ست کنید (`TERMINAL_KEYSTORE_PATH/PASSWORD/ALIAS/KEY_PASSWORD`) — راهنما: `store/RELEASE_SIGNING.md`
2. در CI، سکرت‌های `TERMINAL_KEYSTORE_BASE64`، `TERMINAL_KEYSTORE_PASSWORD`، `TERMINAL_KEY_ALIAS`، `TERMINAL_KEY_PASSWORD` را اضافه کنید
3. تگ `v*` بزنید تا جاب `signed-market-release` APK/AAB امضاشده بسازد و جاب `publish` یک Release با خروجی‌ها بسازد

## تأیید / Verification

```sh
python3 scripts/source_audit.py
python3 scripts/market_release_gate.py
./scripts/verify_jvm.sh
```

## لایسنس / License

[Apache License 2.0](LICENSE)
