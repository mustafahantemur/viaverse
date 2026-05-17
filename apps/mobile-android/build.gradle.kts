plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.viaverse.mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.viaverse.mobile"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":apps:mobile-kmp"))
    implementation("androidx.activity:activity-compose:1.13.0")
}
