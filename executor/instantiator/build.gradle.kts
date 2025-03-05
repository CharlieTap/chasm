plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.runtime.core)
                api(projects.executor.invoker)
                api(projects.ir)
                api(libs.result)

                implementation(projects.irFactory)
                implementation(projects.optimiser)
                implementation(projects.predecoder)
                implementation(projects.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.executor.instantiator)
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.ir)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "instantiator"
    description = "A wasm module instantiator"
}
