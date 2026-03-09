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
                api(projects.runtime.core)
                implementation(projects.executor.invoker)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ir)
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "predecoder"
    description = "predecoding functions for chasms runtime instructions"
}
