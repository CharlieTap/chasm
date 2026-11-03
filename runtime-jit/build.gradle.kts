import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.jit"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.library.bytecode.version.get()))
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
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
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
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-jit"
    description = "Compiles wasm modules to DEX on Android devices and instantiates them on Chasm's runtime"
}
