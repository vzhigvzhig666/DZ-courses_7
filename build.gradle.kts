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
}

tasks.register<Test>("androidTest") {
    description = "Runs black-box tests in Android Chrome through Appium."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    systemProperty("platform", "ANDROID")
}

tasks.register<Test>("iosTest") {
    description = "Runs black-box tests in iOS Safari through Appium."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    systemProperty("platform", "IOS")
}
