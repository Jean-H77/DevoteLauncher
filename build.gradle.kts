import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.devote"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("io.github.vincenzopalazzo:material-ui-swing:1.1.2")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<ShadowJar> {

    archiveBaseName = "DevoteRSPSLauncher"
    archiveClassifier = ""
    archiveVersion = ""

    manifest {
        attributes["Main-Class"] = "org.devote.Main"
    }

    from("./jre") {
        into("jre")
    }
}
