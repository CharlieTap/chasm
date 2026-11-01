plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.executor.interpreter"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.executor.callCore)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.executor.dispatch)
                implementation(projects.host)
                implementation(projects.runtime.resources)
                implementation(projects.test.fixture.runtime)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "interpreter"
    description = "Chasm's interpreter loop and its function invoker"
}
