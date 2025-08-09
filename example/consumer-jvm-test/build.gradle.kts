import io.github.charlietap.chasm.gradle.CodegenConfig
import io.github.charlietap.chasm.gradle.StringEncodingStrategy

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
                generateTypesafeGlobalProperties = true,
            )
            function("pal_string_function") {
                stringReturnType(StringEncodingStrategy.POINTER_AND_LENGTH)
            }
            function("length_prefixed_string_function") {
                stringReturnType(StringEncodingStrategy.LENGTH_PREFIXED)
            }
            function("null_terminated_string_function") {
                stringReturnType(StringEncodingStrategy.NULL_TERMINATED)
            }
            function("packed_i64_string_function") {
                stringReturnType(StringEncodingStrategy.PACKED_POINTER_AND_LENGTH)
            }
        }
    }
}

dependencies {
    implementation(libs.hilt.core)

    ksp(libs.hilt.compiler)
}


