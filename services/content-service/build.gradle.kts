plugins {
    id("viaverse.java-spring-service")
}

description = "Content service owning organic social posts, local announcements, events, and business promotion."

springBoot {
    mainClass.set("app.viaverse.content.ContentServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
}
