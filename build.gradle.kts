import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.5.+" apply false
}

architectury {
    minecraft = "${property("minecraft_version")}"
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = extensions.getByName<LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    dependencies {
        "minecraft"("com.mojang:minecraft:${property("minecraft_version")}")
//        "mappings"("net.fabricmc:yarn:1.20.1+build.10:v2")
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-1.20.1:${property("parchment_version")}@zip")
        })
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")

    base.archivesName.set("${property("archives_base_name")}")
    version = "${property("mod_version")}"
    group = "${property("maven_group")}"

	repositories {
		maven {
            name = "GeckoLib"
            url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
		}
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
	}

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }
}
