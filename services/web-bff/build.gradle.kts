plugins {
    id("viaverse.java-spring-service")
}

description = "Backend-for-Frontend serving Viaverse public web and mobile clients."

springBoot {
    mainClass.set("app.viaverse.webbff.WebBffApplication")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.6")
    implementation("org.springframework.boot:spring-boot-starter-security:4.0.6")
}
