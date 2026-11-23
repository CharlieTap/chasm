package io.github.charlietap.chasm.bindgen.fixture

import io.github.charlietap.chasm.bindgen.CodegenConfig
import io.github.charlietap.chasm.bindgen.CodegenRuntime
import io.github.charlietap.chasm.bindgen.ExecutionTier
import io.github.charlietap.chasm.bindgen.WasiLinking
import io.github.charlietap.chasm.config.RuntimeConfig

internal fun codegenConfig(
    generateTypesafeGlobalProperties: Boolean = false,
    generateTypesafeMemoryProperties: Boolean = false,
    generateSuspendingFactories: Boolean = false,
    runtime: CodegenRuntime = CodegenRuntime.PORTABLE_VM,
    wasi: WasiLinking = WasiLinking.DISABLED,
    executionTier: ExecutionTier = ExecutionTier.Interpreter(),
    runtimeConfig: RuntimeConfig = RuntimeConfig(),
) = CodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
    generateTypesafeMemoryProperties = generateTypesafeMemoryProperties,
    generateSuspendingFactories = generateSuspendingFactories,
    runtime = runtime,
    wasi = wasi,
    executionTier = executionTier,
    runtimeConfig = runtimeConfig,
)
