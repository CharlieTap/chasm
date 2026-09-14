import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.stream"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {

        all {
            languageSettings {

            }
        }

       commonMain {
            dependencies {

            }
        }

        commonTest {
            dependencies {

            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "stream"
    description = "stream source interface"
}
