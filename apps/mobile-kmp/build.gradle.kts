plugins {
    id("viaverse.kotlin-multiplatform-app")
}

description = "Empty Gradle module for the future Viaverse Kotlin Multiplatform mobile app."

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.components.resources)
            }
        }
    }
}
