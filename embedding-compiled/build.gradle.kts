import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.compiled"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.InternalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.embeddingApi)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-compiled"
    description = "Zero-copy access to GC arrays and table elements on Chasm's JIT engine, for embedders and host functions"
}
