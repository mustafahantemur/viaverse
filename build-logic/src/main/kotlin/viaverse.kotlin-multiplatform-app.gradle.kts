plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("viaverse.code-quality")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
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
                implementation(compose.desktop.currentOs)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
