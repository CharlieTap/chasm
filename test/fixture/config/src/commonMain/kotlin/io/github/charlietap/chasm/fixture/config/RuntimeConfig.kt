package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.RuntimeConfig

fun runtimeConfig(
    debugInfo: Boolean = false,
    gcStrategy: GCStrategy = GCStrategy.ARENA,
    gcThreshold: GCThreshold = GCThreshold.MB(8),
) = RuntimeConfig(
    debugInfo = debugInfo,
    gcStrategy = gcStrategy,
    gcThreshold = gcThreshold,
)
