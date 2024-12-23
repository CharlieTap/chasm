pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("../gradle/plugins/linting-conventions")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

include(":android")
include(":benchmark")
include(":fibonacci-wasm")

rootProject.name = "chasm-example"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
