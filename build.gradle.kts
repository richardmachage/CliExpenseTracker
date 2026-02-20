plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "dev.forsythe"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("dev.forsythe.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}