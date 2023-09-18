import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.2-SNAPSHOT" apply false
}

architectury {
    minecraft = "${property("minecraft_version")}"
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    extensions.getByName<LoomGradleExtensionAPI>("loom").apply {
        silentMojangMappingsLicense()
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${property("minecraft_version")}")
        "mappings"("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
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
	}

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }
}
