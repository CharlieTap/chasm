@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
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
    val target: ArtifactTarget = ArtifactTarget.HOTSPOT,
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
