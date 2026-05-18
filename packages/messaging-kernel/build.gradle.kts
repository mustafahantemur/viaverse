plugins {
    id("viaverse.java-library")
}

description = "Transactional outbox pattern for event-emitting backend services."

dependencies {
    api(project(":packages:shared-kernel"))
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.1"))

    // Public API leaks Jakarta + Spring types via @Component / JPA / @Scheduled,
    // so they belong on `api`, not `implementation`.
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework:spring-context")
    api("org.springframework:spring-messaging")
    api("org.springframework.cloud:spring-cloud-stream")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("org.slf4j:slf4j-api")
}
