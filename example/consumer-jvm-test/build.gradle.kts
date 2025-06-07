import io.github.charlietap.chasm.gradle.CodegenConfig

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.symbol.processing)
    alias(libs.plugins.chasm)

    alias(libs.plugins.conventions.linting)
}

chasm {
    modules {
        create("TestService") {
            binary = layout.projectDirectory.file("src/main/resources/test.wasm")
            packageName = "com.test.chasm"
            codegenConfig = CodegenConfig(
                transformStrings = true,
                generateTypesafeGlobalProperties = true,
            )
        }
    }
}

dependencies {
    implementation(libs.chasm.jvm)
    implementation(libs.hilt.core)

    ksp(libs.hilt.compiler)
}


