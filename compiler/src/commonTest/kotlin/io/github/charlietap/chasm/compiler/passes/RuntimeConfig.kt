package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig

internal fun runtimeConfig(
    debugInfo: Boolean = false,
    bytecodeFusion: Boolean = true,
    gcStrategy: GCStrategy = GCStrategy.ARENA,
) = RuntimeConfig(
    debugInfo = debugInfo,
    bytecodeFusion = bytecodeFusion,
    gcStrategy = gcStrategy,
)
