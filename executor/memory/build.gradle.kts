plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.bolt)
}

bolt {
    library = "liblinmem"
    url = "https://github.com/CharlieTap/linmem/releases/download/0.1.48/"
}

kotlin {

    sourceSets {

        all {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

       commonMain {
            dependencies {
                api(projects.ast)
                api(projects.executor.runtimeInternal)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.executor.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "memory"
    description = "chasms memory instance functionality"
}
