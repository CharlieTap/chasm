package io.github.charlietap.chasm.fixture.executor.runtime.stack

import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths

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
