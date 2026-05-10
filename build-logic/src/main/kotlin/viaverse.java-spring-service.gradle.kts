import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")
    id("viaverse.code-quality")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

val envPrefix = project.name.replace("-", "_").uppercase()
val dbMapping = mapOf(
    "identity-service" to "viaverse_identity",
    "marketplace-service" to "viaverse_marketplace",
    "payment-service" to "viaverse_payment",
    "messaging-service" to "viaverse_messaging",
    "media-service" to "viaverse_media",
    "notification-service" to "viaverse_notification",
    "search-service" to "viaverse_search",
    "trust-gamification-service" to "viaverse_trust_gamification",
    "ads-monetization-service" to "viaverse_ads_monetization",
    "admin-bff" to "viaverse_admin_bff"
)

val defaultDbName = dbMapping[project.name]
    ?: "viaverse_${project.name.removeSuffix("-service").replace("-", "_")}".lowercase()

val defaultDbUrl = "jdbc:postgresql://localhost:5432/$defaultDbName"
val defaultDbUsername = "viaverse"
val defaultDbPassword = "viaverse"

dependencies {
    implementation(project(":packages:observability"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

flyway {
    url = providers.environmentVariable("${envPrefix}_DB_URL").orElse(defaultDbUrl).get()
    user = providers.environmentVariable("${envPrefix}_DB_USERNAME").orElse(defaultDbUsername).get()
    password = providers.environmentVariable("${envPrefix}_DB_PASSWORD").orElse(defaultDbPassword).get()
    locations = arrayOf("classpath:db/migration")
}

tasks.named<BootRun>("bootRun") {
    systemProperty(
        "spring.profiles.active",
        providers.gradleProperty("springProfiles").orElse("local").get()
    )
}

tasks.register<BootRun>("bootRunDebug") {
    group = "application"
    description = "Runs this Spring Boot service with a debugger socket enabled."

    val bootRun = tasks.named<BootRun>("bootRun")
    classpath = bootRun.get().classpath
    mainClass.set(providers.provider { bootRun.get().mainClass.get() })

    systemProperty(
        "spring.profiles.active",
        providers.gradleProperty("springProfiles").orElse("local").get()
    )

    debugOptions {
        enabled.set(true)
        server.set(true)
        suspend.set(true)
        port.set(providers.gradleProperty("debugPort").map(String::toInt).orElse(5005))
    }
}
