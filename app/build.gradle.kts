plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseKeystorePath = providers.environmentVariable("TERMINAL_KEYSTORE_PATH").orNull
val releaseStorePassword = providers.environmentVariable("TERMINAL_KEYSTORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("TERMINAL_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("TERMINAL_KEY_PASSWORD").orNull
val releaseSigningReady = listOf(releaseKeystorePath, releaseStorePassword, releaseKeyAlias, releaseKeyPassword).all { !it.isNullOrBlank() }

android {
    namespace = "app.terminalssh.secure"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.terminalssh.secure"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    signingConfigs {
        if (releaseSigningReady) {
            create("marketRelease") {
                storeFile = file(releaseKeystorePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (releaseSigningReady) signingConfig = signingConfigs.getByName("marketRelease")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation("com.github.mwiede:jsch:2.28.6")
    implementation("org.connectbot:termlib:0.1.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    testImplementation(kotlin("test"))
}
