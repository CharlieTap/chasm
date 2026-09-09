plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.fixture.executor.instantiator"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.executor.instantiator)
                api(projects.test.fixture.ast)
                api(projects.test.fixture.runtime)
            }
        }
    }
}
