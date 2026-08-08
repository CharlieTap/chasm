import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.kotlinx.test.resources)
}

val isCi = !System.getenv("GITHUB_ACTIONS").isNullOrEmpty()

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyHierarchyTemplate {
        common {
            group("web") {
                withJs()
                withWasmJs()
            }
            group("nonJs") {
                withJvm()
                withNative()
            }
        }
    }

    js {
        nodejs()
        browser {
            testTask {
                useKarma {
                    if (isCi) {
                        useFirefoxHeadless()
                    } else {
                        useChromeHeadless()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    if (isCi) {
                        useFirefoxHeadless()
                    } else {
                        useChromeHeadless()
                    }
                }
            }
        }
    }

    sourceSets {
        getByName("nonJsMain") {
            kotlin.srcDir("src/nonJsTargetsMain/kotlin")
            dependencies {
                implementation(projects.chasm)
            }
        }

        getByName("commonTest") {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
            }
        }
        getByName("nonJsTest") {
            kotlin.srcDir("src/nonJsTargetsTest/kotlin")
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "vm"
    description = "abstract interface for a wasm virtual machine"
}
