
import io.github.charlietap.sweet.plugin.task.GenerateTestsTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    alias(libs.plugins.kotlinx.test.resources)
    alias(libs.plugins.wasm.testsuite)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

sweet {
    wasmToolsVersion = "1.222.0"
    testSuiteCommit = "68c6f83f331081ba8aaafae3f89ce20d1cc456fb"
    scriptRunner = "io.github.charlietap.chasm.script.ChasmScriptRunner"
    testPackageName = "io.github.charlietap.chasm.testsuite"
    proposals = listOf(
        "multi-memory",
        "exception-handling",
        "gc",
    )
    excludes = listOf(
        "simd_*/**", "**/simd_*",
        "align.wast", "binary.wast", "data.wast", "elem.wast", "global.wast", "imports.wast", "memory.wast",
        "proposals/exception-handling/binary.wast",
        "proposals/exception-handling/imports.wast",
        "proposals/gc/binary.wast",
        "proposals/multi-memory/data.wast",
    )
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.config)
                api(projects.executor.runtimeExternal)
                api(projects.host)
                api(projects.stream)

                implementation(projects.ast)

                implementation(projects.decoder)

                implementation(projects.executor.instantiator)
                implementation(projects.executor.invoker)
                implementation(projects.executor.runtimeInternal)
                implementation(projects.executor.memory)

                implementation(projects.typeSystem)

                implementation(projects.validator)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fake.decoder)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.executor.runtime)

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

tasks.named {
    it.contains("lintKotlinCommonTest")
}.configureEach {
    dependsOn(tasks.withType<GenerateTestsTask>())
}
