plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

kotlin {
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
