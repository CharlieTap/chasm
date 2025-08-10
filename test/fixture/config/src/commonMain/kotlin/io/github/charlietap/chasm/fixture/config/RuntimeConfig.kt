package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.RuntimeConfig

fun runtimeConfig(
    debugInfo: Boolean = false,
) = RuntimeConfig(
    debugInfo = debugInfo,
)
