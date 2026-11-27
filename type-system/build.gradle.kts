plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.type"
    }

    validatePublicAbi()
}

configure<PublishingConventionsExtension> {
    name = "type-system"
    description = "chasms internal type system"
}
