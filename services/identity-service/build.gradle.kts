plugins {
    id("viaverse.java-spring-service")
}

description = "Empty Gradle module for the future Viaverse identity service."

springBoot {
    mainClass.set("app.viaverse.identity.IdentityServiceApplication")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    implementation("org.springframework:spring-aop:7.0.7")
    implementation("org.aspectj:aspectjweaver:1.9.25.1")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:4.0.6")
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.6")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.3")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // Spring Security's Argon2PasswordEncoder delegates to BouncyCastle's
    // Argon2BytesGenerator. Without bcprov the encoder fails at first call
    // with NoClassDefFoundError org.bouncycastle.crypto.params.Argon2Parameters.
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation(libs.spring.cloud.stream)
    implementation(libs.spring.cloud.stream.binder.kafka)
    implementation(libs.libphonenumber)
    implementation("commons-codec:commons-codec:1.20.0")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Observability: Micrometer Tracing -> OpenTelemetry bridge + OTLP exporter.
    // Versions managed by the Spring Boot BOM declared above.
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry:4.0.6")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    // Aligned with the OpenTelemetry API version that Spring Boot 4.0.6 brings
    // (1.55.0). The newer 2.27.0-alpha appender calls
    // LogRecordBuilder.setException(Throwable) which is a 1.61.0+ API.
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.22.0-alpha")

    testImplementation("org.testcontainers:testcontainers:2.0.4")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.4")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.4")
    testImplementation("org.testcontainers:testcontainers-kafka:2.0.4")
}
