plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.address"
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-address"
    description = "runtime addresses for state found in chasms store"
}
