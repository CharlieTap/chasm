plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    sourceSets {
       commonMain {
            dependencies {
                api(projects.runtime.core)
                api(projects.test.fixture.ir)
                api(projects.test.fixture.config)
            }
        }
    }
}
