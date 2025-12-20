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
    testSuiteCommit = "eeac3141ee6de9fdce16d562c1a5a51bd58fbf26"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        Proposal("threads", SemanticPhase.DECODING),
        Proposal("wide-arithmetic", SemanticPhase.EXECUTION)
    )
    excludes = listOf(
        "**/relaxed_*", "**/*_relaxed_*", // Features relaxed simd opcodes
        "simd_*/**", "**/simd_*", // Features simd opcodes
        "**/*64.wast", "**/memory64*", // Features memory64 opcodes
        "table_copy_mixed.wast", // Features memory64 opcodes
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
