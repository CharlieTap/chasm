package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.jvm.size.BytecodeSize
import io.github.charlietap.chasm.compiler.backend.jvm.size.MethodSize
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig

data class JvmOptions(
    val entryClass: String = "chasm.aot.WasmModule",
    val runtime: RuntimeConfig = RuntimeConfig(),
    val memory: MemoryLowering = MemoryLowering.HELPERS,
    val splitInstructions: Int? = null,
    val methodSize: MethodSize = BytecodeSize(),
) {
    val meterFuel: Boolean = runtime.limits.cpu is CpuLimits.Metered

    val interruptible: Boolean = runtime.interrupts

    val stacktraces: Boolean = runtime.stacktraces
}

enum class MemoryLowering {
    HELPERS,

    BYTE_ARRAY,

    BYTE_BUFFER,

    UNSAFE,
}
