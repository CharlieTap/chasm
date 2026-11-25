import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.build.config)

    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

configure<PublishingConventionsExtension> {
    name = "chasm-dex-compiler"
    description = "Compiles wasm modules to DEX for Android's runtime"
}

buildConfig {
    packageName("io.github.charlietap.chasm.dex")
    buildConfigField("VERSION", version.toString())
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.dex"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(libs.kotlinx.coroutines.core)

                implementation(projects.ast)
                implementation(projects.moduleDeclarations)
                implementation(projects.compiler.driver)
                implementation(projects.compiler.frontend)
                implementation(projects.compiler.backendDex)
                implementation(projects.module)
                implementation(projects.runtime.compiledFormat)
                implementation(projects.validator)
                implementation(projects.libs.parallel)
                implementation(projects.libs.parallelCoroutines)
                implementation(libs.result)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.smali.baksmali)
            }
        }
    }
}
