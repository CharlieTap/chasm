import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.LimitedSupport
import io.github.charlietap.sweet.plugin.Proposal
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import org.gradle.api.tasks.testing.Test
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
    wasmToolsVersion = "1.239.0"
    testSuiteCommit = "345367358f065375524498749470720d9cdd1418"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        Proposal("threads", SemanticPhase.DECODING),
        Proposal("wide-arithmetic", SemanticPhase.EXECUTION)
    )
    limitedSupport = listOf(
        LimitedSupport(
            setOf("simd_*/**", "**/simd_*", "**/relaxed_*", "**/*_relaxed_*"),
            SemanticPhase.VALIDATION,
        ),
        LimitedSupport(
            setOf("**/*64.wast", "**/memory64*", "table_copy_mixed.wast"),
            SemanticPhase.VALIDATION,
        ),
    )
}

corpus {
    corpusRef = libs.versions.wasm.corpus.ref
    corpusRunner = "io.github.charlietap.chasm.corpus.ChasmCorpusRunner"
    testPackageName = "io.github.charlietap.chasm.corpus.generated"
    phase = io.github.charlietap.corpus.lib.CorpusPhase.INVOCATION
    versions = listOf("1.0", "2.0", "3.0")
    excludedFeatures = listOf("memory64", "simd", "relaxed-simd")
    excludedTargets = listOf(
        // Huge finite loop; valid corpus case, but too slow for the current JVM interpreter path.
        "learning_rate_scheduling",
    )
}

kotlin {

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled.set(true)
    }

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
                implementation(projects.test.fixture.ir)

                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
                implementation(libs.sweet.lib)
                implementation(libs.corpus.lib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io.core)
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

tasks.withType<Test>().configureEach {
    maxHeapSize = "2g"
    jvmArgs("-Xss32m")
}
