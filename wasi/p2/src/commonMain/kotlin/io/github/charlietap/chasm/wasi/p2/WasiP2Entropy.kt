package io.github.charlietap.chasm.wasi.p2

fun interface WasiP2Entropy {
    fun bytes(size: Int): ByteArray
}
