import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("viaverse.code-quality")
}

extensions.configure<JavaPluginExtension> {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
    options.compilerArgs.add("-parameters")
}

dependencies {
    add("implementation", project(":packages:observability"))
    add("implementation", "org.springframework.boot:spring-boot-starter-web")
    add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
    add("implementation", "org.springframework.boot:spring-boot-starter-validation")
    add("implementation", "org.springframework.boot:spring-boot-starter-opentelemetry:4.0.6")
    add("implementation", "io.micrometer:micrometer-core")
    add("implementation", "io.micrometer:micrometer-registry-prometheus")
    add("implementation", "io.micrometer:micrometer-tracing-bridge-otel")
    add("implementation", "io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.22.0-alpha")
    add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
}

tasks.named<BootRun>("bootRun") {
    systemProperty(
        "spring.profiles.active",
        providers.gradleProperty("springProfiles").orElse("local").get()
    )
    jvmArgs("--sun-misc-unsafe-memory-access=allow")
}

tasks.register<BootRun>("bootRunDebug") {
    group = "application"
    description = "Runs this Spring Boot service with a debugger socket enabled."

    val bootRun = tasks.named<BootRun>("bootRun")
    classpath = bootRun.get().classpath
    mainClass.set(bootRun.flatMap { it.mainClass })

    systemProperty(
        "spring.profiles.active",
        providers.gradleProperty("springProfiles").orElse("local").get()
    )
    jvmArgs("--sun-misc-unsafe-memory-access=allow")

    debugOptions {
        enabled.set(true)
        server.set(true)
        suspend.set(true)
        port.set(providers.gradleProperty("debugPort").map(String::toInt).orElse(5005))
    }
}
