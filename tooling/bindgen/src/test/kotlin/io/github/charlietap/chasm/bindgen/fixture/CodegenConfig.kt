package io.github.charlietap.chasm.bindgen.fixture

import io.github.charlietap.chasm.bindgen.CodegenConfig
import io.github.charlietap.chasm.bindgen.CodegenRuntime
import io.github.charlietap.chasm.bindgen.CpuLimits
import io.github.charlietap.chasm.bindgen.ExecutionTier
import io.github.charlietap.chasm.bindgen.MemoryLimits
import io.github.charlietap.chasm.bindgen.WasiLinking
import io.github.charlietap.chasm.config.GCStrategy

internal fun codegenConfig(
    generateTypesafeGlobalProperties: Boolean = false,
    generateTypesafeMemoryProperties: Boolean = false,
    generateSuspendingFactories: Boolean = false,
    runtime: CodegenRuntime = CodegenRuntime.PORTABLE_VM,
    wasi: WasiLinking = WasiLinking.DISABLED,
    executionTier: ExecutionTier = ExecutionTier.Interpreter(),
    cpu: CpuLimits = CpuLimits.Unmetered(),
    memory: MemoryLimits = MemoryLimits.Unconstrained(),
    interrupts: Boolean = false,
    stacktraces: Boolean = false,
    gcStrategy: GCStrategy? = null,
) = CodegenConfig(
    generateTypesafeGlobalProperties = generateTypesafeGlobalProperties,
    generateTypesafeMemoryProperties = generateTypesafeMemoryProperties,
    generateSuspendingFactories = generateSuspendingFactories,
    runtime = runtime,
    wasi = wasi,
    executionTier = executionTier,
    cpu = cpu,
    memory = memory,
    interrupts = interrupts,
    stacktraces = stacktraces,
    gcStrategy = gcStrategy,
)
