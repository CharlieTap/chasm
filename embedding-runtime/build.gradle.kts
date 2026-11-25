import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.runtime"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.InternalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)

                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.core)
                implementation(projects.runtime.host)
                implementation(projects.runtime.resources)
                implementation(projects.typeSystem)

                implementation(libs.result)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-runtime"
    description = "The stores, instances and handles Chasm's interpreter and compiled code share, and the functions that work on them"
}
