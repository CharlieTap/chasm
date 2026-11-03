import io.github.charlietap.sweet.lib.SemanticPhase
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
    alias(libs.plugins.conventions.sweet)

    alias(libs.plugins.wasm.corpus)
    alias(libs.plugins.kotlinx.test.resources)
}

sweet {
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"

    sources {
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
    excludedTags = listOf("stress-test", "benchmark", "duration-extra-long")
    excludedTargets = listOf(
        // Execution-heavy fixtures; keep the normal corpus run suitable for the development loop.
        "esbuild",
        "jsquash_hqx",
        // Huge finite loop; valid corpus case, but too slow for the current JVM interpreter path.
        "learning_rate_scheduling",
    )
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm"
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
                api(projects.host)
                api(projects.libs.parallel)
                api(projects.runtime.type)
                api(projects.runtime.value)
                api(projects.stream)

                implementation(projects.ast)
                implementation(projects.cir)
                implementation(projects.compiler.backendChasm)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.executor.instantiator)
                implementation(projects.executor.invoker)
                implementation(projects.module)
                implementation(projects.runtime.core)
                implementation(projects.typeSystem)
                implementation(projects.validator)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.memory)
                implementation(projects.runtime.address)
                implementation(projects.test.fake.decoder)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.chasm)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)

                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinx.test.resources)
                implementation(libs.sweet.lib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io.core)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.chasmCoroutines)
                implementation(libs.corpus.lib)
                implementation(libs.wasi.emscripten.host.chasm.wasip1.kmp)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "chasm"
    description = "A wasm runtime for Kotlin Multiplatform"
}
