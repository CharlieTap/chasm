package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.RuntimeConfig

fun runtimeConfig(
    interrupts: Boolean = false,
    stacktraces: Boolean = false,
    limits: Limits = Limits(),
) = RuntimeConfig(
    interrupts = interrupts,
    stacktraces = stacktraces,
    limits = limits,
)
