plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.localmedia"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.localmedia"
        minSdk = 26
        targetSdk = 35
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
        versionCode = 1
        versionName = "1.0"
    }
}
