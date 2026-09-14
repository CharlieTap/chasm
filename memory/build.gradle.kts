plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.memory"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        nativeMain {
            dependencies {
                implementation(projects.libs.mmap)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "memory"
    description = "a kotlin multiplatform wasm linear memory implementation"
}
