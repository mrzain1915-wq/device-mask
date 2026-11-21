plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.device.mask"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.device.mask"
        minSdk = 29
        targetSdk = 36
        versionCode = 67
        versionName = "1.0.28"
    }

    // FIX for "App not installed": pin the debug signing key.
    // The GitHub runner creates a fresh Android debug key on every run,
    // so every rebuild has a DIFFERENT signature and Android refuses to
    // install it over the previous build of the same package. A committed
    // debug.keystore (app/debug.keystore, standard debug credentials
    // below, SHA-256 45:79:98:72:92:BC:2C:12:5D:57:72:E0:25:7E:D4:30:
    // 78:16:DA:1A:6F:DA:5E:03:10:D8:A4:EF:DC:2F:3F:AB) makes every cloud
    // build sign with the SAME key, so updates install over it.
    // This is a debug key only - never use it for a Play Store release.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// AGP 9: android.kotlinOptions is removed — use the top-level kotlin
// compilerOptions block exposed by AGP's built-in Kotlin support.
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // libxposed API is compile-only: the framework provides it at runtime.
    compileOnly(libs.xposed.api)

    // libxposed "service" API: its manifest registers the XposedProvider
    // ContentProvider in this app, which is how the framework reads our
    // settings from the hooked app's process (getRemotePreferences).
    // implementation, not compileOnly: the provider class must be inside
    // this APK so the system can run it.
    implementation(libs.xposed.service)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
}
