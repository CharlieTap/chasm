plugins {
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
                api(projects.ir)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ir)

                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "optimiser"
    description = "bytecode optimiser for chasm ir"
}
