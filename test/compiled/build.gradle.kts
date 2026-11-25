plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.test.compiled"
    }

    compilerOptions {
        optIn.add("io.github.charlietap.chasm.ExperimentalChasmApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.runtimeCompiled)
                api(libs.sweet.lib)

                implementation(projects.ast)
                implementation(projects.decoder)
                implementation(projects.validator)
                implementation(projects.typeSystem)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.result)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.compiler.backendAnalysis)
                implementation(libs.asm.util)
            }
        }
    }
}
