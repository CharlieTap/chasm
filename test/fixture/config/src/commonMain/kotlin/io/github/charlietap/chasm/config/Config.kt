package io.github.charlietap.chasm.config

fun config(
    moduleConfig: ModuleConfig = moduleConfig(),
    runtimeConfig: RuntimeConfig = runtimeConfig(),
) = Config(
    moduleConfig = moduleConfig,
    runtimeConfig = runtimeConfig,
)
