package io.github.charlietap.chasm.config

data class Config(
    val moduleConfig: ModuleConfig = ModuleConfig(),
    val runtimeConfig: RuntimeConfig = RuntimeConfig(),
)
