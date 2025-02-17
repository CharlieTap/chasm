plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(projects.ir)
                api(projects.executor.runtimeInternal)
                api(projects.typeSystemIr)
                api(libs.result)

                implementation(projects.executor.memory)
                implementation(projects.host)
                implementation(projects.libs.sse2)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.executor.runtime)
                implementation(projects.test.fixture.ir)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "invoker"
    description = "A wasm function invoker"
}
