package io.github.charlietap.chasm.fixture.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance

fun frame(
    arity: Int = 0,
    depths: StackDepths = stackDepths(),
    instance: ModuleInstance = moduleInstance(),
    previousFramePointer: Int = 0,
) = ActivationFrame(
    arity = arity,
    depths = depths,
    instance = instance,
    previousFramePointer = previousFramePointer,
)
