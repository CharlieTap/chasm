plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.bolt)
}

bolt {
    library = "liblinmem"
    url = "https://github.com/CharlieTap/linmem/releases/download/0.1.48/"
    linkerOptions = mapOf(
        "mingw_x64" to "-lntdll"
    )
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
                api(projects.runtime.core)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "memory"
    description = "a kotlin multiplatform wasm linear memory implementation"
}
