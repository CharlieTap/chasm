plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    sourceSets {
       commonMain {
            dependencies {
                api(projects.chasm)
                api(projects.test.fixture.runtime)
            }
        }
    }
}
