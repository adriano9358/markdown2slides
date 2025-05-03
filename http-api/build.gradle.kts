plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":services"))
    implementation("org.slf4j:slf4j-api:2.0.17")
    // To use Spring MVC
    implementation("org.springframework:spring-webmvc:6.1.13")
    implementation("org.springframework.security:spring-security-oauth2-client:6.4.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}