package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.CodegenConfig
import io.github.charlietap.chasm.gradle.CodegenRuntime
import io.github.charlietap.chasm.gradle.WasiLinking

internal fun codegenConfig(
    generateTypesafeGlobalProperties: Boolean = false,
    generateTypesafeMemoryProperties: Boolean = false,
    generateSuspendingFactories: Boolean = false,
    runtime: CodegenRuntime = CodegenRuntime.PORTABLE_VM,
    wasi: WasiLinking = WasiLinking.DISABLED,
) = CodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
    generateTypesafeMemoryProperties = generateTypesafeMemoryProperties,
    generateSuspendingFactories = generateSuspendingFactories,
    runtime = runtime,
    wasi = wasi,
)
