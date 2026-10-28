plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.resource"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.core)
                api(libs.result)

                implementation(projects.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-resources"
    description = "Allocates, collects and disposes of a Chasm store's resources"
}
