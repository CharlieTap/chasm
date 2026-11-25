import io.github.charlietap.sweet.lib.SemanticPhase
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
    alias(libs.plugins.conventions.sweet)

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

kotlin {
    android {
        namespace = "io.github.charlietap.chasm"
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.InternalChasmApi")
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.config)
                api(projects.embeddingApi)
                api(projects.libs.parallel)
                api(projects.runtime.type)
                api(projects.stream)
                api(libs.kotlinx.coroutines.core)

                implementation(projects.embeddingRuntime)
                implementation(projects.libs.parallelCoroutines)
                implementation(projects.runtime.value)

                implementation(projects.ast)
                implementation(projects.cir)
                implementation(projects.compiler.backendChasm)
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.executor.callCore)
                implementation(projects.executor.instantiator)
                implementation(projects.memory)
                implementation(projects.module)
                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.address)
                implementation(projects.runtime.core)
                implementation(projects.runtime.host)
                implementation(projects.runtime.resources)
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
                implementation(projects.test.fixture.typeSystem)

                implementation(libs.kotlinx.coroutines.test)
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
