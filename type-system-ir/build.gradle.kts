plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ir)
                api(libs.result)
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
    name = "type-system-ir"
    description = "type system functions for chasms ir"
}
