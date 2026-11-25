plugins {
    alias(libs.plugins.conventions.kmp.jvm)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    android {
        namespace = "io.github.charlietap.chasm.compiler.driver"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ast)
                api(projects.config)
                api(projects.compiler.frontend)
                api(projects.decoder)
                api(projects.runtime.compiledFormat)
                api(projects.stream)
                api(projects.validator)
                api(libs.result)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "compiler-driver"
    description = "Reads, decodes and validates the Wasm modules compilers lower, and maps their code back to the binary"
}
