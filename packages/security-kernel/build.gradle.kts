plugins {
    id("viaverse.java-library")
}

description = "Reusable primitives for validating identity-service issued JWTs."

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api("org.springframework.security:spring-security-oauth2-jose")
    api("org.springframework.security:spring-security-oauth2-resource-server")
}
