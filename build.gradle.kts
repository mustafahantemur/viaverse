plugins {
    base
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    group = "com.viaverse"
    version = "0.1.0-SNAPSHOT"
}

tasks.register("migrateLocal") {
    group = "migration"
    description = "Run Flyway migration for all backend services using local database settings."

    val serviceProjects = listOf(
        project(":services:identity-service"),
        project(":services:marketplace-service"),
        project(":services:payment-service"),
        project(":services:messaging-service"),
        project(":services:media-service"),
        project(":services:notification-service"),
        project(":services:search-service"),
        project(":services:trust-gamification-service"),
        project(":services:ads-monetization-service"),
        project(":services:admin-bff")
    )

    serviceProjects.forEach {
        dependsOn(it.tasks.named("flywayMigrate"))
    }
}
