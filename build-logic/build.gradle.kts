plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:4.0.6")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.7")
    implementation("org.flywaydb:flyway-gradle-plugin:12.6.0")
    implementation("org.flywaydb:flyway-database-postgresql:12.6.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.3.21")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.9.3")
    // Android Gradle plugin — needed by the KMP convention plugin to declare
    // an Android target on shared modules consumed by Android apps.
    implementation("com.android.tools.build:gradle:9.0.0")
}

// Gradle 9.4 can otherwise schedule the Kotlin compile before all generated
// convention-plugin sources are materialized on a cold or partially cleaned build.
tasks.named("compileKotlin") {
    dependsOn(
        "generateScriptPluginAdapters",
        "generatePrecompiledScriptPluginAccessors",
    )
}
