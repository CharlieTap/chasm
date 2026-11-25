import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.corpus)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.test.resources)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.corpus"
        minSdk = 30
        withHostTest {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    listOf(macosArm64(), linuxX64()).forEach { target ->
        target.binaries {
            test(listOf(NativeBuildType.RELEASE))
        }
        target.testRuns.named("test") {
            setExecutionSourceFrom(target.binaries.getTest(NativeBuildType.RELEASE))
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmAndAndroid") {
                group("jvmHost") {
                    withJvm()
                    withCompilations { compilation ->
                        compilation.target.platformType == KotlinPlatformType.androidJvm && compilation.name != "deviceTest"
                    }
                }
                withCompilations { compilation -> compilation.target.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.ExperimentalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.chasm)
                api(libs.corpus.lib)

                implementation(projects.runtime.core)
                implementation(projects.typeSystem)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.result)
                implementation(libs.wasi.emscripten.host.chasm.wasip1.kmp)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.test.resources)
            }
        }

        named("jvmHostTest") {
            dependencies {
                implementation(projects.chasmJvmCompiler)
                implementation(projects.runtimeCompiled)
            }
        }

        named("androidDeviceTest") {
            dependencies {
                implementation(projects.chasmDexCompiler)
                implementation(projects.runtimeCompiled)
                implementation(libs.kotlin.junit)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}

corpus {
    deviceKeepRules.from(
        layout.projectDirectory.file("harness-rules.pro"),
        layout.settingsDirectory.file("runtime-compiled/consumer-rules.pro"),
    )
}
