import org.flywaydb.gradle.FlywayExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("viaverse.java-spring-app")
    id("org.flywaydb.flyway")
}

val envPrefix = project.name.replace("-", "_").uppercase()
val dbMapping = mapOf(
    "identity-service" to "viaverse_identity",
    "profile-service" to "viaverse_profile",
    "content-service" to "viaverse_content",
    "marketplace-service" to "viaverse_marketplace",
    "payment-service" to "viaverse_payment",
    "messaging-service" to "viaverse_messaging",
    "media-service" to "viaverse_media",
    "notification-service" to "viaverse_notification",
    "search-service" to "viaverse_search",
    "trust-gamification-service" to "viaverse_trust_gamification",
    "ads-monetization-service" to "viaverse_ads_monetization"
)

val defaultDbName = dbMapping[project.name]
    ?: "viaverse_${project.name.removeSuffix("-service").replace("-", "_")}".lowercase()

val defaultDbUrl = "jdbc:postgresql://localhost:5432/$defaultDbName"
val defaultDbUsername = "viaverse"
val defaultDbPassword = "viaverse"

dependencies {
    add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.1"))
    add("implementation", project(":packages:web-kernel"))
    add("implementation", project(":packages:messaging-kernel"))
    add("implementation", project(":packages:security-kernel"))
    add("implementation", "org.springframework.boot:spring-boot-starter-data-jpa")
    add("implementation", "org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    add("implementation", "org.flywaydb:flyway-core")
    add("implementation", "org.flywaydb:flyway-database-postgresql")
    add("implementation", "org.springframework.cloud:spring-cloud-stream")
    add("implementation", "org.springframework.cloud:spring-cloud-stream-binder-kafka")
    add("implementation", "org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.3")
    add("implementation", "org.mapstruct:mapstruct:1.6.3")
    add("annotationProcessor", "org.mapstruct:mapstruct-processor:1.6.3")
    add("runtimeOnly", "org.postgresql:postgresql")

    add("testImplementation", "org.testcontainers:testcontainers:2.0.4")
    add("testImplementation", "org.testcontainers:testcontainers-junit-jupiter:2.0.4")
    add("testImplementation", "org.testcontainers:testcontainers-postgresql:2.0.4")
    add("testImplementation", "org.testcontainers:testcontainers-kafka:2.0.4")
}

extensions.configure<FlywayExtension> {
    url = providers.environmentVariable("${envPrefix}_DB_URL").orElse(defaultDbUrl).get()
    user = providers.environmentVariable("${envPrefix}_DB_USERNAME").orElse(defaultDbUsername).get()
    password = providers.environmentVariable("${envPrefix}_DB_PASSWORD").orElse(defaultDbPassword).get()
    locations = arrayOf("filesystem:${project.projectDir}/src/main/resources/db/migration")
}
