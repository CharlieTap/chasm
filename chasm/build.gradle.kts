import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.PhaseLimit
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.wasm.testsuite)
    alias(libs.plugins.wasm.corpus)
    alias(libs.plugins.kotlinx.test.resources)
}

sweet {
    wasmToolsVersion = "1.253.0"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"

    sources {
        register("core") {
            repositoryUrl = "https://github.com/WebAssembly/testsuite.git"
            revision = "345367358f065375524498749470720d9cdd1418"
            testDirectory = "."
            includes = listOf(
                "*.wast",
                "proposals/threads/*.wast",
                "proposals/wide-arithmetic/*.wast",
                "simd_*/**",
                "**/simd_*",
                "**/relaxed_*",
                "**/*_relaxed_*",
                "**/*64.wast",
                "**/memory64*",
                "table_copy_mixed.wast",
            )
            phaseSupport = SemanticPhase.EXECUTION
            phaseLimits = listOf(
                PhaseLimit(
                    patterns = setOf("proposals/threads/**"),
                    phaseSupport = SemanticPhase.DECODING,
                ),
                PhaseLimit(
                    patterns = setOf(
                        "simd_*/**",
                        "**/simd_*",
                        "**/relaxed_*",
                        "**/*_relaxed_*",
                    ),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
                PhaseLimit(
                    patterns = setOf(
                        "**/*64.wast",
                        "**/memory64*",
                        "table_copy_mixed.wast",
                    ),
                    phaseSupport = SemanticPhase.VALIDATION,
                ),
            )
        }

        register("componentModel") {
            repositoryUrl = "https://github.com/WebAssembly/component-model.git"
            revision = "7c720726f183a8c809889dc46716bc6df0dd225d"
            testDirectory = "test"
            includes = listOf("**/*.wast")
            phaseSupport = SemanticPhase.VALIDATION
        }
    }
}

corpus {
    corpusRef = libs.versions.wasm.corpus.ref
    corpusRunner = "io.github.charlietap.chasm.corpus.ChasmCorpusRunner"
    testPackageName = "io.github.charlietap.chasm.corpus.generated"
    phase = io.github.charlietap.corpus.lib.CorpusPhase.INVOCATION
    versions = listOf("1.0", "2.0", "3.0")
    excludedFeatures = listOf("memory64", "simd", "relaxed-simd")
    excludedTags = listOf("stress-test", "benchmark")
    excludedTargets = listOf(
        // Execution-heavy fixtures; keep the normal corpus run suitable for the development loop.
        "esbuild",
        "jsquash_hqx",
        // Huge finite loop; valid corpus case, but too slow for the current JVM interpreter path.
        "learning_rate_scheduling",
    )
}

kotlin {

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    sourceSets {
       commonMain {
            dependencies {
                api(projects.config)
                api(projects.host)
                api(projects.runtime.type)
                api(projects.runtime.value)
                api(projects.stream)

                implementation(projects.ast)
                implementation(projects.decoder)
                implementation(projects.executor.instantiator)
                implementation(projects.executor.invoker)
                implementation(projects.memory)
                implementation(projects.runtime.core)
                implementation(projects.runtime.address)
                implementation(projects.typeSystem)
                implementation(projects.validator)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fake.decoder)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.chasm)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)

                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
                implementation(libs.sweet.lib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io.core)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.corpus.lib)
                implementation(libs.wasi.emscripten.host.chasm.emscripten)
                implementation(libs.wasi.emscripten.host.chasm.wasip1)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "chasm"
    description = "A wasm runtime for Kotlin Multiplatform"
}

tasks.withType<ConfigurableKtLintTask>().configureEach {
    dependsOn(tasks.withType<GenerateTestsTask>())
}
