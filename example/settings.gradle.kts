pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
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
include(":consumer-android-fibonacci")
include(":consumer-jvm-test")
include(":consumer-multiplatform-factorial")
include(":producer")


rootProject.name = "chasm-example"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
