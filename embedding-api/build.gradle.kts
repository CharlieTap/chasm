import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.api"
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
                api(projects.runtime.type)
                api(projects.typeSystem)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-api"
    description = "The stores, modules, instances, calls, values, errors and host functions shared by every Chasm engine"
}
