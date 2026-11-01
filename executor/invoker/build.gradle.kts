plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.executor.invoker"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.executor.callCore)
                api(projects.executor.interpreter)
                api(projects.executor.dispatch)
                api(projects.runtime.core)
                api(projects.runtime.operations)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.host)
                implementation(projects.test.fixture.runtime)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "invoker"
    description = "A wasm function invoker"
}
