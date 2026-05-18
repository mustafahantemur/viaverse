plugins {
    id("viaverse.java-library")
}

description = "Shared web-facing primitives for backend services."

dependencies {
    api(project(":packages:shared-kernel"))
    api(project(":packages:observability"))
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework:spring-aop")
    api("org.aspectj:aspectjweaver")
    api("io.opentelemetry:opentelemetry-api")

    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework:spring-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
