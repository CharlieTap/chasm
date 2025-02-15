plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

configure<PublishingConventionsExtension> {
    name = "host"
    description = "host system interface"
}
