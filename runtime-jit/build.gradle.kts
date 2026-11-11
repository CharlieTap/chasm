import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

val generateChasmVersion = tasks.register("generateChasmVersion") {
    // The version publishing uses: version-name unless the build overrides it.
    val version = project.version.toString()
    val output = layout.buildDirectory.dir("generated/chasmVersion")
    inputs.property("version", version)
    outputs.dir(output)
    doLast {
        val file = output.get().file("io/github/charlietap/chasm/jit/internal/ChasmVersion.kt").asFile
        file.parentFile.mkdirs()
        file.writeText("package io.github.charlietap.chasm.jit.internal\n\ninternal const val CHASM_VERSION = \"$version\"\n")
    }
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.jit"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.library.bytecode.version.get()))
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            kotlin.srcDir(generateChasmVersion)
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(projects.embeddingModule)
                api(projects.embeddingRuntime)

                implementation(projects.ast)
                implementation(projects.compiler.backendDex)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.executor.instantiator)
                implementation(projects.module)
                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.compiled)
                implementation(projects.runtime.core)
                implementation(libs.result)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(projects.chasm)
                implementation(libs.kotlin.junit)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-jit"
    description = "Compiles wasm modules to DEX on Android devices and instantiates them on Chasm's runtime"
}
