# JSch uses algorithm names/reflection internally; preserve public JSch API.
-keep class com.jcraft.jsch.** { *; }
-dontwarn org.bouncycastle.**

# JSch optional dependencies not used on Android:
-dontwarn com.sun.jna.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.ietf.jgss.**
-dontwarn org.newsclub.net.unix.**
-dontwarn org.slf4j.**
