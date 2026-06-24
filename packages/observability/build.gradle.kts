plugins {
    id("viaverse.java-library")
}

description = "Shared backend observability foundation for correlation, errors, and audit."

dependencies {
    api(project(":packages:shared-kernel"))
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api("jakarta.servlet:jakarta.servlet-api")
    api("org.slf4j:slf4j-api")
    api("io.micrometer:micrometer-core")
    api("io.opentelemetry:opentelemetry-api")
    api("io.opentelemetry:opentelemetry-sdk")
    api("io.opentelemetry.semconv:opentelemetry-semconv")
    api("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.22.0-alpha")
    api("ch.qos.logback:logback-classic")
    api("org.springframework.boot:spring-boot")
    api("org.springframework:spring-context")
    api("org.springframework:spring-web")
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
