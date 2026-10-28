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

                implementation(projects.host)
                implementation(projects.runtime.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.executor.dispatch)
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "interpreter"
    description = "Chasm's interpreter loop and its function invoker"
}
