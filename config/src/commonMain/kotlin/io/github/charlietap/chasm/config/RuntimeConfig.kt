package io.github.charlietap.chasm.config

data class RuntimeConfig(
    val interrupts: Boolean = false,
    val stacktraces: Boolean = false,
    val limits: Limits = Limits(),
)
