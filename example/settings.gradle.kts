pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("..")
    includeBuild("../gradle/plugins/linting-conventions")
}

plugins {
    id("com.gradle.develocity") version ("4.3.2")
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"

        publishing.onlyIf { _ -> !System.getenv("GITHUB_ACTIONS").isNullOrEmpty() }
    }
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
    }
}

includeBuild("..") {
    dependencySubstitution {
        substitute(module("io.github.charlietap.chasm:vm")).using(project(":vm"))
        substitute(module("io.github.charlietap.chasm:vm-jvm")).using(project(":vm"))
        substitute(module("io.github.charlietap.chasm:chasm")).using(project(":chasm"))
        substitute(module("io.github.charlietap.chasm:chasm-jvm")).using(project(":chasm"))
    }
}

include(":android")
include(":benchmark")
include(":binary")
include(":consumer-android-fibonacci")
include(":consumer-jvm-test")
include(":consumer-multiplatform-factorial")
include(":producer")
include(":web")

rootProject.name = "chasm-example"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
