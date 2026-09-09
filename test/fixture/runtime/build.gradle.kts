plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.fixture.runtime"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.runtime.core)
                api(projects.test.fixture.ast)
                api(projects.test.fixture.config)
            }
        }
    }
}
