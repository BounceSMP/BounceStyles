pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.architectury.dev/") { name = "Architectury" }
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9+"
}

stonecutter {

    create(rootProject) {
        fun addVersion(version: String, vararg loaders: String) = loaders.forEach { version("$version-$it", version) }

        addVersion("1.21.11", "fabric", "neoforge")
        addVersion("1.21.1", "fabric", "neoforge")

        vcsVersion = "1.21.11-fabric"
    }
}