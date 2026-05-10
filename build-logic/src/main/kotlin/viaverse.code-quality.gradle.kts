plugins {
    base
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events("failed", "skipped")
    }
}

