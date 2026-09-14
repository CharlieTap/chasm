plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.parallel"
    }
}

configure<PublishingConventionsExtension> {
    name = "parallel"
    description = "Parallel task execution contracts for Chasm"
}
