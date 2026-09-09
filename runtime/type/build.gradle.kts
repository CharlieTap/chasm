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
    android {
        namespace = "io.github.charlietap.chasm.runtime.type"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.typeSystem)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
