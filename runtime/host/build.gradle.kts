plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.host"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.embeddingApi)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-host"
    description = "The interpreter's internal host-call convention and the host interfaces its heap implements"
}
