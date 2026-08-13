import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.chasm)

                implementation(projects.compiler)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "chasm-coroutines"
    description = "Coroutine support for Chasm"
}
