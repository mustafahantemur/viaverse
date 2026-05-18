plugins {
    id("viaverse.java-spring-service")
}

description = "Empty Gradle module for the future Viaverse identity service."

springBoot {
    mainClass.set("app.viaverse.identity.IdentityServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    implementation("org.springframework:spring-aop:7.0.7")
    implementation("org.aspectj:aspectjweaver:1.9.25.1")
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
    implementation(libs.libphonenumber)
    implementation("commons-codec:commons-codec:1.20.0")
}
