plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.typeSystem)
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
