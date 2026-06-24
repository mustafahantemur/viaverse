plugins {
    id("viaverse.java-spring-bff")
}

description = "Standalone mock Web BFF for shaping the Viaverse authenticated product prototype."

springBoot {
    mainClass.set("app.viaverse.mockwebbff.MockWebBffApplication")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    runtimeOnly("org.postgresql:postgresql")
}
