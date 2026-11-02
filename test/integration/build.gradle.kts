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
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmAndAndroid") {
                withJvm()
                withCompilations { it.target.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.chasm)
                implementation(projects.test.fixture.typeSystem)
                implementation(libs.kotlinx.test.resources)
            }
        }

        named("jvmAndAndroidTest") {
            dependencies {
                implementation(projects.runtimeAot)
                implementation(projects.tooling.compiler)
            }
        }
    }
}
