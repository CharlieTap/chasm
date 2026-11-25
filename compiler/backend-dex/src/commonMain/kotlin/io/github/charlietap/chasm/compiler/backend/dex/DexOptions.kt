package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig

data class DexOptions(
    val entryClass: String = "chasm.dex.WasmModule",
    val runtime: RuntimeConfig = RuntimeConfig(),
    val inlineMemory: Boolean = true,
    val byteArrayBaseOffset: Long? = null,
    val splitInstructions: Int? = null,
) {
    val meterFuel: Boolean = runtime.limits.cpu is CpuLimits.Metered

    val interruptible: Boolean = runtime.interrupts

    val stacktraces: Boolean = runtime.stacktraces
}
