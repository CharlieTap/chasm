plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

configure<PublishingConventionsExtension> {
    name = "runtime-address"
    description = "runtime addresses for state found in chasms store"
}
