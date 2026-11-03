package io.github.charlietap.chasm.compiler.backend.dex

data class DexOptions(
    val packageName: String = "chasm.jit",
    val meterFuel: Boolean = false,
    val interruptible: Boolean = false,
    val inlineMemory: Boolean = true,
    val byteArrayBaseOffset: Long? = null,
    val stacktraces: Boolean = false,
    val splitInstructions: Int? = null,
)
