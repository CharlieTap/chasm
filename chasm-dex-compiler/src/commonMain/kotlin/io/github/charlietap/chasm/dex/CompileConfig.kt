package io.github.charlietap.chasm.dex

import io.github.charlietap.chasm.config.RuntimeConfig

data class CompileConfig(
    val className: String,
    val runtime: RuntimeConfig = RuntimeConfig(),
)
