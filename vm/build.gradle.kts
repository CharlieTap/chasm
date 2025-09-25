import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.kotlinx.test.resources)
}

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled.set(true)
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyHierarchyTemplate {
        common {
            withJs()
            group("nonJs") {
                withJvm()
                withNative()
            }
        }
    }

    js {
        nodejs()
        browser()
    }

    sourceSets {
        val commonMain by getting

        val nonJsMain by getting {
            kotlin.srcDir("src/nonJsTargetsMain/kotlin")
            dependencies {
                implementation(projects.chasm)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
            }
        }
        val nonJsTest by getting {
            kotlin.srcDir("src/nonJsTargetsTest/kotlin")
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "vm"
    description = "abstract interface for a wasm virtual machine"
}
