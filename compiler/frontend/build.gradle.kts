plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.frontend"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.cir)
                api(projects.config)
                api(projects.libs.parallel)
                api(libs.result)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)

                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler-frontend"
    description = "Compiles WebAssembly modules to Chasm IR"
}
