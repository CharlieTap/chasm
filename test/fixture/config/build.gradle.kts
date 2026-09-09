plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.fixture.config"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.config)
            }
        }
    }
}
