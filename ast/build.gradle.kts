plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.ast"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.moduleDeclarations)
                api(projects.typeSystem)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "ast"
    description = "A abstract syntax for a wasm module encoded Kotlin Multiplatform"
}
