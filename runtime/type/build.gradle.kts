plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

configure<PublishingConventionsExtension> {
    name = "runtime-type"
    description = "types introduced by chasms runtime"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.typeSystem)
            }
        }
    }
}
