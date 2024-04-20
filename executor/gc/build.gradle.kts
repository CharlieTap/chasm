import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    id("kmp-conventions")
    id("linting-conventions")
    id("publishing-conventions")
}

kotlin {

    sourceSets {

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }

        commonMain {
            dependencies {

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
    name = "gc"
    description = "chasms gc utils"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}
