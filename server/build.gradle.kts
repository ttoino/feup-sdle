val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.0.2"
}

group = "pt.up.fe.sdle"
version = "0.0.1"

application {
    mainClass.set("pt.up.fe.sdle.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.7")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:2.3.7")
    implementation("io.ktor:ktor-server-call-id-jvm:2.3.7")
    implementation("io.ktor:ktor-server-caching-headers-jvm:2.3.7")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.3.7")

    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")
}
