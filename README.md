# ترمینال SSH / Terminal SSH

[![CI](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/actions/workflows/android-release.yml/badge.svg)](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/actions)
[![Release](https://img.shields.io/github/v/release/mohamad1313m13-cyber/terminal-ssh-android?sort=semver)](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases)
[![License](https://img.shields.io/github/license/mohamad1313m13-cyber/terminal-ssh-android)](LICENSE)

کلاینت SSH آزاد و متن‌باز برای اندروید، با ترمینال واقعی VT100/ANSI، تأیید سختگیرانهٔ کلید سرور، و رابط کاربری فارسی تاریک. نسخهٔ فعلی: **0.3.1**.

A free, open-source Android SSH client with a real VT100/ANSI terminal, strict host-key
verification, and a Persian-first dark UI. Current version: **0.3.1**.

## دانلود / Download

نصب‌پذیرترین نسخه (APK دیباگ) را از بخش [Releases](https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases/latest) دریافت کنید. نسخه‌های امضاشده بازار فقط پس از پیکربندی کی‌استور ناشر ساخته می‌شوند (به `store/RELEASE_SIGNING.md` مراجعه کنید).

## ویژگی‌ها / Features

- اتصال SSH با رمز عبور، و کلید خصوصی (وارد کردن از فایل)
- ترمینال واقعی `xterm-256color` روی termlib/libvterm
- تولبار کلیدهای خاص: Esc، Tab، Ctrl/Alt چسبان، ^C/^D/^L، فلش‌ها، Home/End/PgUp/PgDn
- چند سشن هم‌زمان به‌صورت تب، زنده‌مانده با Foreground Service و نوتیفیکیشن «n سشن فعال»
- اتصال مجدد خودکار (حداکثر ۳ بار) هنگام قطع موقت شبکه؛ Keepalive هر ۳۰ ثانیه
- سرورهای ذخیره‌شده با نام، گروه، برچسب، نشان‌کردن و جستجو
- TOFU با نمایش کامل اثر انگشت SHA-256، و رد کامل تغییر کلید سرور
- تأیید قبل از چسباندن متن چندخطی؛ شش پوستهٔ ترمینال؛ اندازهٔ قلم قابل تنظیم
- ورود اختیاری با حساب Google (برای همگام‌سازی/بازیابی چنددستگاهی در آینده — برای SSH لازم نیست)
- فارسی + RTL کامل، انگلیسی به‌عنوان زبان دوم
- Per-ABI APK splits (~۹ مگابایت به‌جای ۲۸ مگابایت APK یکپارچه)

## امنیت / Security

- اعتبارسنجی Host Key اجباری است؛ هرگز `StrictHostKeyChecking=no` استفاده نمی‌شود
- رمزها و کلیدها فقط به‌صورت ciphertext با AndroidKeyStore + AES-GCM ذخیره می‌شوند
- `android:allowBackup="false"` و `android:usesCleartextTraffic="false"`
- گیت امنیتی خودکار: `python3 scripts/source_audit.py`

## ساخت / Build

پیش‌نیازها: JDK 17، Android SDK 36، Gradle 8.13 (wrapper همراه پروژه است).

```sh
./gradlew testReleaseUnitTest    # تست‌های واحد
./gradlew lintRelease            # لینت
./gradlew assembleDebug          # APK دیباگ
./gradlew assembleRelease        # APK امضاشده (نیاز به متغیرهای کی‌استور)
./gradlew bundleRelease          # AAB امضاشده (نیاز به متغیرهای کی‌استور)
./scripts/verify_jvm.sh          # گیت‌های JVM (لازم است kotlinc در PATH باشد)
```

خروجی‌ها در `app/build/outputs/` قرار می‌گیرند. GitHub Actions همین گیت‌ها را روی هر push/PR اجرا می‌کند و APK را به‌صورت artifact آپلود می‌کند.

## نسخه بازار / Market release

متن‌های فارسی برای کافه‌بازار و سیاست حریم خصوصی در پوشه `store/` آماده است. برای انتشار امضاشده:

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
