plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

group = "io.github.charlietap.sweet"


kotlin {

    sourceSets {
       commonMain {
            dependencies {
                api(libs.kotlinx.serialization)
            }
        }
    }
}
