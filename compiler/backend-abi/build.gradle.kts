plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.backend.abi"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.cir)
                api(projects.typeSystem)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.runtime.compiled)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-abi"
    description = "The contract between code compiled from Chasm IR and the runtime it binds to"
}
