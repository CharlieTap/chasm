plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

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
