import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.2.2"
}

// TODO: Update the group to yours
group = "yedev.allaydev.allaybuilder"
// TODO: Update the description to yours
description = "ybuilder for allay"
version = "0.2.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // TODO: Update the version of api to the latest
    compileOnly(group = "org.allaymc.allay", name = "api", version = "0.12.0")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveFileName = "${project.name}-${version}-shaded.jar"
}
