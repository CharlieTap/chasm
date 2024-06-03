import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.java.bytecode.version.get())
    }
}
