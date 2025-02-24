plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.typeSystem)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "ast"
    description = "A abstract syntax for a wasm module encoded Kotlin Multiplatform"
}
