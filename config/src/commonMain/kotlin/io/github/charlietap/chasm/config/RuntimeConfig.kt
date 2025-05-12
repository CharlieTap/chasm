package io.github.charlietap.chasm.config

data class RuntimeConfig(
    val debugInfo: Boolean = false,
    val bytecodeFusion: Boolean = true,
    val gcStrategy: GCStrategy = GCStrategy.ARENA,
    val gcThreshold: GCThreshold = GCThreshold.MB(8),
)
