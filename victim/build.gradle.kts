plugins {
    kotlin("jvm") version "1.8.0"
}

group = "cheatahh.jvm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // the lib containing the vulnerability were exploiting
    implementation("org.apache.commons:commons-collections4:4.0")
}