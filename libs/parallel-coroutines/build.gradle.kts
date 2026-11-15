plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.parallel.coroutines"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.parallel)
                api(libs.kotlinx.coroutines.core)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "parallel-coroutines"
    description = "Coroutine-backed parallel task execution for Chasm"
}
