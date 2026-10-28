plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.backend.chasm"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.cir)
                api(projects.libs.parallel)
                api(projects.runtime.core)
                api(libs.result)

                implementation(projects.executor.invoker)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.compiler.frontend)
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fixture.runtime)
                implementation(libs.kotlin.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-chasm"
    description = "Links Chasm IR into the bytecode Chasm's interpreter runs"
}
