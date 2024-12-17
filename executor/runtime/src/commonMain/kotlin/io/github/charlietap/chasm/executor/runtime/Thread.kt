package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction

data class Thread(
    val frame: Stack.Entry.ActivationFrame,
    val instructions: Array<DispatchableInstruction>,
)
