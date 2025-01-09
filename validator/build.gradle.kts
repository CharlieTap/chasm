plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {

       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.config)
                api(libs.result)

                implementation(projects.typeSystem)
                implementation(projects.libs.stack)
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
    name = "validator"
    description = "An api for module validation embedded in Kotlin Multiplatform"
}
