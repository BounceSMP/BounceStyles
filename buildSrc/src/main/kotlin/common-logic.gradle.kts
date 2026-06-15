import dev.kikugie.stonecutter.build.StonecutterBuildExtension

plugins {
    `java`
}

val stonecutter = project.extensions.getByType(StonecutterBuildExtension::class)

val loader = project.name.split("-")[1]
val mcVersion = property("deps.minecraft") as String

version = "$mcVersion+${property("mod.version")}"
base.archivesName = "${property("mod.id")}-$loader"

repositories {

}

dependencies {

}

java {
    withSourcesJar()
    val javaCompat =
    when {
        stonecutter.eval(mcVersion, ">=26.1") -> JavaVersion.VERSION_25
        stonecutter.eval(mcVersion, ">=1.20.5") -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["version"] = prop("mod.version")
        this["minecraft"] = prop("deps.minecraft")
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}