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
                api(libs.result)

                implementation(projects.compiler)
                implementation(projects.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.executor.instantiator)
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "instantiator"
    description = "A wasm module instantiator"
}
