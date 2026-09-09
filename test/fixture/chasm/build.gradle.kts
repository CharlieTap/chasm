plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.fixture.chasm"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.chasm)
                api(projects.test.fixture.host)
                api(projects.test.fixture.runtime)
            }
        }
    }
}
