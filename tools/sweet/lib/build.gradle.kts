plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

group = "io.github.charlietap.sweet"


kotlin {
    android {
        namespace = "io.github.charlietap.sweet.lib"
    }

    sourceSets {
       commonMain {
            dependencies {
                api(libs.kotlinx.serialization)
            }
        }
    }
}
