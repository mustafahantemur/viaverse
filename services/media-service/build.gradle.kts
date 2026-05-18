plugins {
    id("viaverse.java-spring-service")
}

description = "Media service owning upload sessions, object keys, and media asset lifecycle."

springBoot {
    mainClass.set("app.viaverse.media.MediaServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
    implementation(platform("software.amazon.awssdk:bom:2.44.4"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:auth")
}
