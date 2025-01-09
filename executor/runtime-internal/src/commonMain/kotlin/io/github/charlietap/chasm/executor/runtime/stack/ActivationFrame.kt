package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class ActivationFrame(
    val arity: Int,
    val depths: StackDepths,
    val instance: ModuleInstance,
    var locals: MutableList<ExecutionValue>,
)
