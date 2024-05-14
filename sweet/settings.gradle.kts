pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("../gradle/plugins/kmp-conventions")
    includeBuild("../gradle/plugins/linting-conventions")
    includeBuild("../gradle/plugins/publishing-conventions")
    includeBuild("../gradle/plugins/versions-conventions")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include("lib")
include("plugin")

rootProject.name = "sweet-gradle-plugin"
