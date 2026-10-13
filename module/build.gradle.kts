plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.module"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.config)
                api(projects.decoder)
                api(projects.validator)
                api(projects.libs.parallel)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "module"
    description = "Decodes and validates wasm modules in a single pass for Kotlin Multiplatform"
}
