package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.instance.ModuleInstance

data class ActivationFrame(
    val arity: Int,
    val depths: StackDepths,
    val instance: ModuleInstance,
    val previousFramePointer: Int = 0,
)
