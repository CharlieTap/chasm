package io.github.charlietap.chasm.compiler.backend.abi

data class Fingerprint(
    val module: String,
    val shape: String = "",
    val runtimeAbi: Int,
    val meterFuel: Boolean,
    val interruptible: Boolean,
) {
    companion object {
        const val RUNTIME_ABI = 8
    }
}
