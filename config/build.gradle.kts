import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.config"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()
}

configure<PublishingConventionsExtension> {
    name = "config"
    description = "configuration for chasm"
}
