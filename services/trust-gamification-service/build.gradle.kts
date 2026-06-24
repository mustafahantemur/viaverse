plugins {
    id("viaverse.java-spring-service")
}

description = "Trust and gamification service owning trust signals, score snapshots, and future verification flows."

springBoot {
    mainClass.set("app.viaverse.trustgamification.TrustGamificationServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
}
