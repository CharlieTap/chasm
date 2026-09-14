plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

configure<PublishingConventionsExtension> {
    name = "runtime-value"
    description = "stack values from chasms runtime"
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.value"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.address)
                api(projects.typeSystem)
            }
        }
    }
}
