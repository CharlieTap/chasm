plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)

    alias(libs.plugins.kotlinx.test.resources)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xwarning-level=ASSIGNED_VALUE_IS_NEVER_READ:disabled")
    }

    sourceSets {
       commonMain {
            dependencies {

                api(projects.ast)
                api(projects.config)
                api(projects.stream)

                api(libs.result)

                implementation(projects.typeSystem)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
                implementation(projects.test.fixture.config)
                implementation(projects.test.fake.decoder)

                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "wasm-parser"
    description = "A wasm binary decoder for Kotlin Multiplatform"
}
