import org.gradle.kotlin.dsl.`maven-publish`

plugins {
	id("net.fabricmc.fabric-loom-remap")
	`maven-publish`
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

loom {
    mods {  }
}

repositories {
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    maven("https://maven.architectury.dev/")
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroupAndSubgroups("com.geckolib")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("minecraft_version")}:${property("parchment_version")}@zip")
    })
	modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    modImplementation("dev.architectury:architectury-fabric:${property("architectury_version")}")
    modImplementation("software.bernie.geckolib:geckolib-fabric-${property("minecraft_version")}:${property("geckolib_version")}")


    modCompileOnly("maven.modrinth:flashback:AGkqd25Y")
}

tasks.processResources {
	val version = version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand(
            "id" to providers.gradleProperty("mod_id").get(),
            "version" to version,
            "minecraft_version" to providers.gradleProperty("minecraft_version").get(),
            "name" to providers.gradleProperty("mod_name").get(),
            "description" to providers.gradleProperty("mod_description").get(),
            "authors" to providers.gradleProperty("mod_authors").get(),
            "website" to providers.gradleProperty("mod_website").get(),
            "source_url" to providers.gradleProperty("mod_source_url").get(),
            "issue_tracker" to providers.gradleProperty("mod_issue_tracker").get(),
            "license" to providers.gradleProperty("mod_license").get(),
            "icon" to providers.gradleProperty("mod_icon").get(),
            "loader_version" to providers.gradleProperty("loader_version").get(),
            "architectury_version" to providers.gradleProperty("architectury_version").get(),
            "geckolib_version" to providers.gradleProperty("geckolib_version").get(),
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
