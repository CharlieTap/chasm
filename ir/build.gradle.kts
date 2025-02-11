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
                api(projects.ast)
            }
        }

        commonTest {
            dependencies {

            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "ir"
    description = "intermediate representation of chasms internal bytecode"
}
