plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    alias(libs.plugins.kotlinx.test.resources)

    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xsuppress-warning=ASSIGNED_VALUE_IS_NEVER_READ")
    }

    sourceSets {
       commonMain {
            dependencies {

                api(projects.ast)
                api(projects.stream)

                api(libs.result)

                implementation(projects.typeSystem)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.test.fixture.ast)
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
