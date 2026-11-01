pluginManagement {
    repositories {
        gradlePluginPortal()
        google {
            content {
                includeGroupByRegex("androidx(\\..*)?")
                includeGroupByRegex("com\\.android(\\..*)?")
                includeGroup("com.google.testing.platform")
            }
        }
        mavenCentral()
    }

    includeBuild("../../gradle/plugins/kotlin-conventions")
    includeBuild("../../gradle/plugins/linting-conventions")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("androidx(\\..*)?")
                includeGroupByRegex("com\\.android(\\..*)?")
                includeGroup("com.google.testing.platform")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

include("lib")
include("plugin")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "corpus-gradle-plugin"
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
