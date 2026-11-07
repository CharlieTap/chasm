import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.kotlinx.test.resources)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.integration"
        minSdk = 30
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmAndAndroid") {
                group("jvmHost") {
                    withJvm()
                    withCompilations { it.target.platformType == KotlinPlatformType.androidJvm && it.name != "deviceTest" }
                }
                withCompilations { it.target.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.chasm)
                implementation(projects.runtime.core)
                implementation(projects.test.fixture.typeSystem)
                implementation(projects.typeSystem)
                implementation(libs.kotlinx.test.resources)
            }
        }

        named("jvmHostTest") {
            dependencies {
                implementation(projects.chasmAot)
                implementation(projects.runtimeAot)
            }
        }

        named("androidDeviceTest") {
            dependencies {
                implementation(projects.chasmJit)
                implementation(libs.kotlin.junit)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}
