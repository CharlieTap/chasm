package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance

fun publicInstance(
    moduleInstance: ModuleInstance = moduleInstance(),
): Instance = Instance(moduleInstance)
