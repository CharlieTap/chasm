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
                implementation(projects.executor.invoker)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ir)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "predecoder"
    description = "predecoding functions for chasms runtime instructions"
}
