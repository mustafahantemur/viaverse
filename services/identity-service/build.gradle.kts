plugins {
    id("viaverse.java-spring-service")
}

description = "Empty Gradle module for the future Viaverse identity service."

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    implementation("org.springframework:spring-aop:7.0.7")
    implementation("org.aspectj:aspectjweaver:1.9.25.1")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:4.0.6")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // Observability: Micrometer Tracing -> OpenTelemetry bridge + OTLP exporter.
    // Versions managed by the Spring Boot BOM declared above.
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")

    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
}
