package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig

fun config(
    moduleConfig: ModuleConfig = moduleConfig(),
    runtimeConfig: RuntimeConfig = runtimeConfig(),
) = Config(
    moduleConfig = moduleConfig,
    runtimeConfig = runtimeConfig,
)
