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

    sourceSets {
        commonMain {
            dependencies {
                api(projects.host)
                api(projects.runtime.type)
                api(projects.typeSystem)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-api"
    description = "The module information and type builders Chasm's entry points share"
}
