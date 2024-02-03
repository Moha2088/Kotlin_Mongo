plugins {
    kotlin("jvm") version "1.9.20"
    application
    kotlin("plugin.scripting") version "1.9.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.slf4j:slf4j-api:1.8.2")
    implementation("ch.qos.logback:logback-classic:1.4.12")



    implementation("org.mongodb:mongodb-driver-reactivestreams:4.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")

    implementation("com.github.kittinunf.fuel:fuel:3.0.0-alpha1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")


}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}