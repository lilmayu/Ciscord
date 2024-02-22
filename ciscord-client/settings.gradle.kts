pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "ciscord-client"
include(":app")
include(":commons")
project(":commons").projectDir = file("../../Ciscord/ciscord-server/modules/commons")
