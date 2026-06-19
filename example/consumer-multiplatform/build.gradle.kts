import io.github.charlietap.chasm.gradle.CodegenConfig
import io.github.charlietap.chasm.gradle.CodegenTask
import io.github.charlietap.chasm.gradle.ExportedAllocator
import io.github.charlietap.chasm.gradle.StringEncodingStrategy
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.gradle.kotlin.dsl.withType
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.chasm)
    alias(libs.plugins.metro)

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
                stringParam("input")
                stringReturnType()
            }
            function("truncate_null_terminated") {
                stringParam("input", StringEncodingStrategy.NULL_TERMINATED)
                stringReturnType()
            }
            function("truncate_len_prefixed") {
                stringParam("input", StringEncodingStrategy.LENGTH_PREFIXED)
                stringReturnType()
            }
            function("truncate_packed") {
                stringParam("input", StringEncodingStrategy.PACKED_POINTER_AND_LENGTH)
                stringReturnType()
            }
        }
        create("InteropService") {
            binary = layout.projectDirectory.file("src/commonMain/resources/interop.wasm")
            packageName = "com.test.chasm"
            codegenConfig = CodegenConfig(
                generateTypesafeGlobalProperties = true,
            )
            function("pointer_and_length_string") {
                stringReturnType(StringEncodingStrategy.POINTER_AND_LENGTH)
            }
            function("length_prefixed_string") {
                stringReturnType(StringEncodingStrategy.LENGTH_PREFIXED)
            }
            function("null_terminated_string") {
                stringReturnType(StringEncodingStrategy.NULL_TERMINATED)
            }
            function("packed_i64_string") {
                stringReturnType(StringEncodingStrategy.PACKED_POINTER_AND_LENGTH)
            }
        }
    }
}

kotlin {
    jvm()
    js {
        nodejs()
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.binary)
            }
        }
    }
}

tasks.withType<ConfigurableKtLintTask>().configureEach {
    dependsOn(tasks.withType<CodegenTask>())
}
