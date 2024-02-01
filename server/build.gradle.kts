val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    id("io.ktor.plugin") version "2.3.8"
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
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-auto-head-response-jvm")
    implementation("io.ktor:ktor-server-auto-head-response")
    implementation("io.ktor:ktor-server-caching-headers-jvm")
    implementation("io.ktor:ktor-server-call-id-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-forwarded-header-jvm")
    implementation("io.ktor:ktor-server-forwarded-header")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
}
