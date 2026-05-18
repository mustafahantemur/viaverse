import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    // AGP 9 requires the new com.android.kotlin.multiplatform.library
    // plugin when paired with org.jetbrains.kotlin.multiplatform.
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("viaverse.code-quality")
}

val composeDependencies = extensions.getByType(ComposeExtension::class.java).dependencies

extensions.configure<KotlinMultiplatformExtension> {
    jvm("desktop") {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
    }
    // AGP's Android-KMP plugin contributes its own Android target type.
    // Configure that target directly here so this convention plugin does not
    // depend on precompiled-script DSL accessors for the nested `android {}` block.
    @Suppress("UnstableApiUsage")
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        namespace = "app.viaverse.mobile.shared"
        compileSdk = 36
        minSdk = 26
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {}
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(composeDependencies.runtime)
                implementation(composeDependencies.foundation)
                implementation(composeDependencies.material3)
                implementation("io.ktor:ktor-client-core:3.4.3")
                implementation("io.ktor:ktor-client-content-negotiation:3.4.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.3")
                implementation("io.insert-koin:koin-core:4.2.1")
                implementation("com.arkivanov.decompose:decompose:3.5.0")
                implementation("app.cash.sqldelight:runtime:2.3.2")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(composeDependencies.desktop.currentOs)
                implementation("io.ktor:ktor-client-cio:3.4.3")
            }
        }

        // The new Android KMP target exposes the source set as
        // `androidMain` (same naming as androidTarget did).
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:3.4.3")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// Android namespace / SDK live on the Android KMP target above.
