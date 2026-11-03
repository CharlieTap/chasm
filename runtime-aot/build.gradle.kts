import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
    alias(libs.plugins.conventions.sweet)

    alias(libs.plugins.wasm.corpus)
}

corpus {
    corpusRef = libs.versions.wasm.corpus.ref
    corpusRunner = "io.github.charlietap.chasm.aot.corpus.AotCorpusRunner"
    testPackageName = "io.github.charlietap.chasm.aot.corpus.generated"
    phase = io.github.charlietap.corpus.lib.CorpusPhase.INVOCATION
    versions = listOf("1.0", "2.0", "3.0")
    excludedFeatures = listOf("memory64", "simd", "relaxed-simd")
    excludedTags = listOf("stress-test", "benchmark", "duration-extra-long")
    excludedTargets = listOf(
        "esbuild",
        "jsquash_hqx",
        "learning_rate_scheduling",
    )
    corpusBaselineDirectory = layout.settingsDirectory.dir("baselines/corpus/aot")
}

sweet {
    scriptRunner = "io.github.charlietap.chasm.aot.script.AotScriptRunner"
    testPackageName = "io.github.charlietap.chasm.aot.testsuite"
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.aot"
        withHostTest {}
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.ExperimentalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.embeddingRuntime)
                api(projects.runtime.compiledApi)

                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.compiled)

                implementation(projects.executor.instantiator)
                implementation(projects.runtime.core)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.chasm)
                implementation(projects.test.compiled)
                implementation(projects.compiler.backendJvm)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.module)
                implementation(projects.runtime.address)
                implementation(projects.validator)

                implementation(libs.asm.util)
                implementation(libs.corpus.lib)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.sweet.lib)
                implementation(libs.wasi.emscripten.host.chasm.wasip1.kmp)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Xss32m")
    val properties = if (name == "testAndroidHostTest") {
        listOf("chasm.test.aot.split", "chasm.test.aot.memory")
    } else {
        listOf("chasm.test.aot.split")
    }
    properties.forEach { property ->
        providers.systemProperty(property).orNull?.let { value ->
            systemProperty(property, value)
            inputs.property(property, value)
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-aot"
    description = "Runs Wasm modules compiled ahead of time to JVM classes on Chasm's runtime"
}
