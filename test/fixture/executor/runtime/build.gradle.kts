plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    sourceSets {
       commonMain {
            dependencies {
                api(projects.executor.runtimeInternal)
                api(projects.test.fixture.ir)
                api(projects.test.fixture.config)
            }
        }
    }
}
