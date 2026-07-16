plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.component)
                api(projects.test.fixture.runtime)
                api(projects.test.fixture.typeSystem)
            }
        }
    }
}
