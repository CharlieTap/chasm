plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

group = "io.github.charlietap.corpus"
version = libs.versions.version.name.get()

kotlin {
    android {
        namespace = "io.github.charlietap.corpus.lib"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.io.core)
                api(libs.kotlinx.serialization)

                implementation(libs.kotlinx.test.resources)
            }
        }
    }
}
