package io.github.charlietap.chasm.config

data class RuntimeConfig(
    /**
     * When Wasm code fails, include a stack trace in the error showing where
     * it failed. This adds some overhead. roughly 1% to execution time.
     */
    val debugInfo: Boolean = false,
    val gcStrategy: GCStrategy = GCStrategy.ARENA,
    val gcThreshold: GCThreshold = GCThreshold.MB(8),
    val linearMemory: LinearMemoryConfig = LinearMemoryConfig(),
)
