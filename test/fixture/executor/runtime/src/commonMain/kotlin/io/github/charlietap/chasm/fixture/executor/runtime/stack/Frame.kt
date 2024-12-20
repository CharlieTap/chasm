package io.github.charlietap.chasm.fixture.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance

fun frame(
    arity: Int = 0,
    depths: StackDepths = stackDepths(),
    instance: ModuleInstance = moduleInstance(),
    locals: MutableList<ExecutionValue> = mutableListOf(),
) = ActivationFrame(
    arity = arity,
    depths = depths,
    locals = locals,
    instance = instance,
)
