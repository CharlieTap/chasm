plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.runtime.compiled"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtime.core)

                implementation(projects.executor.callCore)
                implementation(projects.runtime.operations)
                implementation(projects.memory)
                implementation(projects.host)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "runtime-compiled"
    description = "The runtime code compiled from Wasm calls into: calls, traps, memory, tables and the GC heap"
}
