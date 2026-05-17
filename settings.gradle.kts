pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "viaverse"

include(
    ":services:identity-service",
    ":services:marketplace-service",
    ":services:payment-service",
    ":services:messaging-service",
    ":services:media-service",
    ":services:notification-service",
    ":services:search-service",
    ":services:trust-gamification-service",
    ":services:ads-monetization-service",
    ":services:admin-bff",
    ":services:web-bff",
    ":apps:mobile-kmp",
    ":apps:web-next",
    ":apps:admin-next",
    ":packages:api-contracts",
    ":packages:shared-kernel",
    ":packages:observability",
)

