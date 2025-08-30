import io.github.charlietap.chasm.gradle.CodegenConfig
import io.github.charlietap.chasm.gradle.ExportedAllocator
import io.github.charlietap.chasm.gradle.StringEncodingStrategy

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.symbol.processing)
    alias(libs.plugins.chasm)

    alias(libs.plugins.conventions.linting)
}

chasm {
    modules {
        create("FactorialService") {
            binary = layout.projectDirectory.file("src/commonMain/resources/factorial.wasm")
            packageName = "com.test.chasm"
            codegenConfig = CodegenConfig(
                generateTypesafeGlobalProperties = true,
            )
        }
        create("StringService") {
            binary = layout.projectDirectory.file("src/commonMain/resources/truncate.wasm")
            packageName = "com.test.chasm"
            allocator = ExportedAllocator("malloc", "free")
            function("truncate") {
                stringParam("input", false)
                stringReturnType()
            }
            function("truncate_null_terminated") {
                stringParam("input", false, encodingStrategy = StringEncodingStrategy.NULL_TERMINATED)
                stringReturnType()
            }
            function("truncate_len_prefixed") {
                stringParam("input", false, encodingStrategy = StringEncodingStrategy.LENGTH_PREFIXED)
                stringReturnType()
            }
            function("truncate_packed") {
                stringParam("input", true, encodingStrategy = StringEncodingStrategy.PACKED_POINTER_AND_LENGTH)
                stringReturnType()
            }
        }
    }
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.hilt.core)
        }
    }

    dependencies {
        add("kspJvm", libs.hilt.compiler)
    }
}


