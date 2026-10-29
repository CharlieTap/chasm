plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.backend.analysis"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.cir)
                api(projects.compiler.backendAbi)

                implementation(projects.runtime.encoding)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.compiler.frontend)
                implementation(projects.decoder)
                implementation(projects.validator)
                implementation(libs.kotlin.test)
                implementation(libs.result)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-analysis"
    description = "What compiler backends lowering Chasm IR to native code need to know about its instructions"
}
