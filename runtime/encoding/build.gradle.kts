plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

configure<PublishingConventionsExtension> {
    name = "runtime-encoding"
    description = "How chasm's runtime encodes values as longs, shared by its interpreter and compiler"
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.encoding"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.value)
            }
        }
    }
}
