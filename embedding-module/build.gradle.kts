import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.module"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)

                implementation(projects.ast)
                implementation(projects.module)
                implementation(projects.moduleDeclarations)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-module"
    description = "The decoded module handle Chasm's entry points create and instantiate"
}
