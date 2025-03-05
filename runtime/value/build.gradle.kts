plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

configure<PublishingConventionsExtension> {
    name = "runtime-value"
    description = "stack values from chasms runtime"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.address)
                api(projects.typeSystem)
            }
        }
    }
}
