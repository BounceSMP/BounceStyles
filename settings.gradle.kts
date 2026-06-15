pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9+"
}

stonecutter {
    create(rootProject) {
        fun addVersion(version: String, vararg loaders: String) = loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

        addVersion("1.21.11", "fabric", "neoforge")

        vcsVersion = "1.21.11-fabric"
    }
}