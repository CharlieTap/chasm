package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance

fun publicInstance(
    config: RuntimeConfig = runtimeConfig(),
    moduleInstance: ModuleInstance = moduleInstance(),
): Instance = Instance(
    config = config,
    instance = moduleInstance,
)
