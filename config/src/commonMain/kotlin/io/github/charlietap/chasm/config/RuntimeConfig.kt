package io.github.charlietap.chasm.config

data class RuntimeConfig(
    val debugInfo: Boolean = false,
) {
    companion object {
        fun default() = RuntimeConfig(
            debugInfo = false,
        )
    }
}
