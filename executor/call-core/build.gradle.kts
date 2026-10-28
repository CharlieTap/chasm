plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.executor.callcore"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.core)
                api(projects.embeddingApi)
                api(libs.result)

                implementation(projects.host)
                implementation(projects.runtime.resources)
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
    name = "call-core"
    description = "Function calls shared by Chasm's interpreter and compiled code"
}
