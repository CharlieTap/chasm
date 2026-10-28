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
                api(projects.executor.dispatch)
                api(projects.runtime.core)
                api(libs.result)

                implementation(projects.host)
                implementation(projects.runtime.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.runtime)
                implementation(projects.test.fixture.ast)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "invoker"
    description = "A wasm function invoker"
}
