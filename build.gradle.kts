@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    kotlin("jvm")
    id("architectury-plugin")
    id("dev.architectury.loom-remap")
    id("dev.kikugie.fletching-table")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp")
}

val loader = project.name.split("-")[1]

group = property("mod.group") as String
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id")}-$loader"

architectury {
    platformSetupLoomIde()
    if (isFabric()) fabric()
    else neoForge()
}

repositories {
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    maven("https://maven.neoforged.net/releases/")
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev/")
        content {
            includeGroup("dev.architectury")
        }
    }
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroupAndSubgroups("com.geckolib")
        }
    }
    maven {
        name = "Curseforge"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Configurable"
        url = uri("https://maven.bawnorton.com/releases")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
    })

    if (isFabric()) {
        //Fabric Dependencies
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric.loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric.api")}")
        val modules = listOf("transitive-access-wideners-v1", "registry-sync-v0", "resource-loader-v0")
        for (it in modules) modImplementation(fabricApi.module("fabric-$it", property("deps.fabric.api") as String))

        modCompileOnly(fletchingTable.modrinth("flashback", sc.current.version, "fabric"))
    }
    else {
        //Neoforge Dependencies
        "neoForge"("net.neoforged:neoforge:${property("deps.neoforge")}")
    }

    modImplementation("dev.architectury:architectury-$loader:${property("deps.architectury")}")
    modImplementation("software.bernie.geckolib:geckolib-$loader-${sc.current.version}:${property("deps.geckolib")}")

    var configurable = "com.bawnorton.configurable:configurable-$loader:${property("deps.configurable")}+${sc.current.version}"
    modImplementation(configurable)
    annotationProcessor(configurable)
    include(configurable)
}

val accessWidener = when {
    sc.eval(sc.current.version, "<=1.21.1") -> "1.21.1.accesswidener"
    else -> "1.21.8+.accesswidener"
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/$accessWidener")
}

stonecutter {
    replacements.string(current.parsed >= "1.21.11") {
        replace("ResourceLocation", "Identifier")

        replace("org.jetbrains.annotations.Nullable", "org.jspecify.annotations.Nullable")
        replace("org.jetbrains.annotations.NotNull", "org.jspecify.annotations.NonNull")
        replace("@NotNull", "@NonNull")

        replace("net.minecraft.client.model.PlayerModel", "net.minecraft.client.model.player.PlayerModel")
    }

    replacements.string(current.parsed >= "1.21.10") {
        replace("PlayerRenderState", "AvatarRenderState")
    }

    replacements.string(current.parsed >= "1.21.9", "avatar") {
        replace("entity.player.Player", "entity.Avatar")
        replace("Player", "Avatar")
    }

    replacements.string(current.parsed >= "1.21.5") {
        replace("software.bernie.geckolib.cache.GeckoLibCache", "software.bernie.geckolib.cache.GeckoLibResources")
    }

    replacements.string(current.parsed >= "1.20.5") {
        replace("software.bernie.geckolib.core", "software.bernie.geckolib")
    }
}

fletchingTable {
    if (isNeoforge()) {
        accessConverter.register(sourceSets.main) {
            add(accessWidener)
        }
    }

    mixins.all {
        automatic = false
    }
    mixins.create("main") {
        mixin("default", "${property("mod.id")}.mixins.json")
    }
}

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

fabricApi {
    configureDataGeneration() {
        outputDirectory = file("$rootDir/src/main/generated")
        client = true
    }
}

fun getJavaVersion(): JavaVersion {
    return when {
        sc.eval(sc.current.version, ">=26.1") -> JavaVersion.VERSION_25
        sc.eval(sc.current.version, ">=1.20.5") -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }
}

java {
    withSourcesJar()
    val javaCompat = getJavaVersion()
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

tasks.named<ProcessResources>("processResources") {
    val mcVersion = sc.current.version
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["id"] = prop("mod.id")
        this["name"] = prop("mod.name")
        this["version"] = prop("mod.version")
        this["minecraft"] = mcVersion
        this["authors"] = prop("mod.authors")
        this["description"] = prop("mod.description")
        this["website"] = prop("mod.website")
        this["source_url"] = prop("mod.source")
        this["issue_tracker"] = prop("mod.issues")
        this["license"] = "MIT"
        this["icon"] = "icon_bounce_styles.png"
        this["mixin"] = "${prop("mod.id")}.mixins.json"
        this["fabric_loader"] = prop("deps.fabric.loader")
        this["java"] = "${getJavaVersion()}"
        this["architectury_version"] = prop("deps.architectury")
        this["geckolib_version"] = prop("deps.geckolib")
        this["neoforge"] = prop("deps.neoforge")
    }

    val mixin = "${prop("mod.id")}.mixins.json"
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", mixin)) {
        expand(props)
    }

    dependsOn(":${stonecutter.current.project}:stonecutterGenerate")
}

tasks {
    processResources {
        if (isFabric())
            exclude("**/neoforge.mods.toml")
        else
            exclude("**/fabric.mod.json", "**/*.classtweaker")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

    type = STABLE
    displayName = "${property("mod.name")} - ${property("mod.version")} - ${loader.uppercaseFirstChar()} - ${stonecutter.current.version}"
    version = "${property("mod.version")}-${loader}-${stonecutter.current.version}"
    changelog = provider { rootProject.file("changelogs/CHANGELOG-${property("mod.version")}.md").readText() }
    modLoaders.add(loader)

    dryRun = true

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)

        if (isFabric())
            requires("fabric-api")

        requires {
            slug = "architectury-api"
        }

        requires {
            slug = "geckolib"
        }
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
        changelogType = "markdown"

        if (isFabric())
            requires("fabric-api")

        client = true
        server = true
    }
}

fun isFabric(): Boolean {
    return loader == "fabric"
}

fun isNeoforge(): Boolean {
    return !isFabric()
}