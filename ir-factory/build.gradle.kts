plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    sourceSets {
       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.ir)
            }
        }

        commonTest {
            dependencies {

            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "ir-factory"
    description = "factory functions for creating ir from ast encodings"
}
