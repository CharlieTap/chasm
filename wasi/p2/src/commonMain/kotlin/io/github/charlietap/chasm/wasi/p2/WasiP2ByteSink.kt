package io.github.charlietap.chasm.wasi.p2

fun interface WasiP2ByteSink {
    fun write(bytes: ByteArray)

    fun flush() = Unit
}

/** Signals an expected sink failure that is returned to the guest as a WASI stream error. */
class WasiP2ByteSinkFailure(
    val reason: String,
) : Exception(reason)
