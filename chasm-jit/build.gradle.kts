import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")

    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.sweet)

    alias(libs.plugins.wasm.corpus)
}

corpus {
    corpusRef = libs.versions.wasm.corpus.ref
    corpusRunner = "io.github.charlietap.chasm.jit.corpus.DexCorpusRunner"
    testPackageName = "io.github.charlietap.chasm.jit.corpus.generated"
    phase = io.github.charlietap.corpus.lib.CorpusPhase.INVOCATION
    versions = listOf("1.0", "2.0", "3.0")
    excludedFeatures = listOf("memory64", "simd", "relaxed-simd")
    excludedTags = listOf("stress-test", "benchmark", "duration-extra-long")
    excludedTargets = listOf(
        "esbuild",
        "jsquash_hqx",
        "learning_rate_scheduling",
    )
}

sweet {
    scriptRunner = "io.github.charlietap.chasm.jit.script.DexScriptRunner"
    testPackageName = "io.github.charlietap.chasm.jit.testsuite"
}

val sweetResources = tasks.register<Sync>("sweetResources") {
    dependsOn("prepareTestSuite")
    from(sweet.intermediatesDirectory) {
        into("sweet")
    }
    into(layout.buildDirectory.dir("generated/sweetResources"))
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.jit"
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
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(projects.embeddingModule)
                api(projects.embeddingRuntime)
                api(projects.stream)

                implementation(projects.ast)
                implementation(projects.decoder)
                implementation(projects.runtimeJit)
                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.compiled)
                implementation(libs.sweet.lib)
                implementation(libs.corpus.lib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlin.test)
            }
        }

        getByName("androidDeviceTest") {
            resources.srcDir(sweetResources)
            dependencies {
                implementation(libs.kotlin.junit)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.runner)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "chasm-jit"
    description = "Compiles wasm modules to DEX on Android devices and runs them on Chasm's runtime"
}
