import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.Proposal
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.wasm.testsuite)
    alias(libs.plugins.kotlinx.test.resources)
}

sweet {
    wasmToolsVersion = "1.239.0"
    testSuiteCommit = "a8101597d3c3c660086c3cd1eedee608ff18d3c3"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        Proposal("threads", SemanticPhase.DECODING),
        Proposal("wasm-3.0", SemanticPhase.EXECUTION),
    )
    excludes = listOf(
        "**/relaxed_*", "**/*_relaxed_*",
        "simd_*/**", "**/simd_*",
        "**/*64.wast", "**/memory64*",
        "align.wast", "binary.wast", "data.wast", "elem.wast", "global.wast", "imports.wast", "memory.wast",
        "proposals/wasm-3.0/binary-leb128.wast", // Features memory64 opcodes
        "proposals/wasm-3.0/table.wast", // Features memory64 opcodes
        "proposals/wasm-3.0/table_copy_mixed.wast", // Features memory64 opcodes
        "proposals/wasm-3.0/table_grow.wast", // Features memory64 opcodes
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
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.io.core)
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
