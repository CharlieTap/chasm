package io.github.charlietap.chasm.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

data class ActivationFrame(
    val arity: Int,
    val depths: StackDepths,
    val instance: ModuleInstance,
    val previousFramePointer: Int = 0,
)
