package io.github.charlietap.chasm.config

data class RuntimeConfig(
    val debugInfo: Boolean = false,
    @Deprecated(
        message = "Bytecode fusion is always enabled. This property is ignored and will be removed in a future major release.",
    )
    val bytecodeFusion: Boolean = true,
    val gcStrategy: GCStrategy = GCStrategy.ARENA,
    val gcThreshold: GCThreshold = GCThreshold.MB(8),
)
