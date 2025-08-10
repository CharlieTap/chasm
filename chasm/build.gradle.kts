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
    wasmToolsVersion = "1.236.0"
    testSuiteCommit = "88e97b0f742f4c3ee01fea683da130f344dd7b02"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        Proposal("multi-memory"),
        Proposal("exception-handling"),
        Proposal("gc"),
        Proposal("threads", SemanticPhase.DECODING),
        Proposal("wasm-3.0", SemanticPhase.VALIDATION),
    )
    excludes = listOf(
        "**/relaxed_*", "**/*_relaxed_*",
        "simd_*/**", "**/simd_*",
        "align.wast", "binary.wast", "data.wast", "elem.wast", "global.wast", "imports.wast", "memory.wast",
        "proposals/exception-handling/binary.wast",
        "proposals/exception-handling/imports.wast",
        "proposals/gc/binary.wast",
        "proposals/multi-memory/data.wast",
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
