plugins {
    id("viaverse.java-library")
}

description = "Shared backend observability foundation for correlation, errors, and audit."

dependencies {
    api(project(":packages:shared-kernel"))
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api("jakarta.servlet:jakarta.servlet-api")
    api("org.slf4j:slf4j-api")
    api("org.springframework:spring-context")
    api("org.springframework:spring-web")
}
