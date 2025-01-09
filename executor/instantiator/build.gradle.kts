plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.executor.runtimeInternal)
                api(projects.executor.invoker)
                api(libs.result)

                implementation(projects.typeSystem)
                implementation(projects.executor.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.executor.instantiator)
                implementation(projects.test.fixture.executor.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "instantiator"
    description = "A wasm module instantiator"
}
