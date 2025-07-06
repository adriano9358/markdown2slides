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
    api(project(":repository"))
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.jdbi:jdbi3-kotlin:3.37.1")
    implementation("org.jdbi:jdbi3-postgres:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
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
    systemProperty("DB_URL", "jdbc:postgresql://localhost:5432/m2sTests?user=postgres&password=postgres")
    environment("SPRING_PROFILES_ACTIVE", "test")
}