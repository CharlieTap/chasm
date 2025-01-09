package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame

data class Thread(
    val frame: ActivationFrame,
    val instructions: Array<DispatchableInstruction>,
)
