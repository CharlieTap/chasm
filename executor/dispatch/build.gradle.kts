plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.executor.dispatch"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.executor.callCore)
                api(projects.runtime.core)
                api(projects.runtime.operations)
                api(libs.result)

                implementation(projects.memory)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.host)
                implementation(projects.test.fixture.runtime)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "dispatch"
    description = "The interpreter's instructions, linked for dispatch"
}
