dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../libs.versions.toml"))
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "versions-conventions"
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")
