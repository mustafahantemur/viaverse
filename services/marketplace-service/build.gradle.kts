plugins {
    id("viaverse.java-spring-service")
}

description = "Marketplace service owning requests, offers, and jobs."

springBoot {
    mainClass.set("app.viaverse.marketplace.MarketplaceServiceApplication")
}

dependencies {
    implementation(project(":packages:api-contracts"))
}
