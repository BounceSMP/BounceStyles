plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    fun plugin(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    implementation(plugin("dev.kikugie.stonecutter", "0.9+"))
}