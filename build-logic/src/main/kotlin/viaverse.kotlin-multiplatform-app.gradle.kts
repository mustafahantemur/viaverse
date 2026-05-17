import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("viaverse.code-quality")
}

val composeDependencies = extensions.getByType(ComposeExtension::class.java).dependencies

extensions.configure<KotlinMultiplatformExtension> {
    jvm("desktop")

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

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
