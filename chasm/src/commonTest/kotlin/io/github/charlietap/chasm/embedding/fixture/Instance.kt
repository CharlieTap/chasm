package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

fun publicInstance(
    config: RuntimeConfig = runtimeConfig(),
    moduleInstance: ModuleInstance = moduleInstance(),
): Instance = Instance(
    config = config,
    instance = moduleInstance,
)
