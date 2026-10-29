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
                api(projects.host)
                api(projects.runtime.type)
                api(projects.runtime.value)

                implementation(projects.executor.callCore)
                implementation(projects.memory)
                implementation(projects.runtime.address)
                implementation(projects.runtime.core)
                implementation(projects.runtime.resources)
                implementation(projects.typeSystem)

                implementation(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.typeSystem)

                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "embedding-runtime"
    description = "The stores, instances and handles Chasm's interpreter and compiled code share, and the functions that work on them"
}
