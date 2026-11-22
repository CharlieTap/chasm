import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.embedding.runtime"
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
                api(projects.runtime.type)

                implementation(projects.executor.callCore)
                implementation(projects.memory)
                implementation(projects.moduleDeclarations)
                implementation(projects.runtime.address)
                implementation(projects.runtime.core)
                implementation(projects.runtime.host)
                implementation(projects.runtime.resources)
                implementation(projects.runtime.value)
                implementation(projects.typeSystem)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.ast)
                implementation(projects.compiler.backendChasm)
                implementation(projects.compiler.frontend)
                implementation(projects.embeddingModule)
                implementation(projects.executor.instantiator)
                implementation(projects.libs.parallelCoroutines)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.typeSystem)

                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-runtime"
    description = "The stores, instances and handles Chasm's interpreter and compiled code share, and the functions that work on them"
}
