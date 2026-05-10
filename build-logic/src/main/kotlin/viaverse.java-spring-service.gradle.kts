import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("viaverse.code-quality")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

dependencies {
    implementation(project(":packages:observability"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
