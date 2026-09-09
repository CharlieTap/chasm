plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.fixture.ast"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.typeSystem)
                api(projects.test.fixture.typeSystem)
            }
        }
    }
}
