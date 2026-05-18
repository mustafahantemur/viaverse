plugins {
    id("viaverse.java-spring-service")
}

description = "Profile service owning public identity, preferences, blocks, and capabilities."

springBoot {
    mainClass.set("app.viaverse.profile.ProfileServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
}
