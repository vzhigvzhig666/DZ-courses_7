plugins {
    kotlin("jvm") version "1.9.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.3")
    testImplementation("io.appium:java-client:10.1.1")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.49.0")
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
}

tasks.register("androidTest") {
    description = "Runs the Wikipedia Android app tests through Appium."
    group = "verification"
    dependsOn(tasks.test)
}
