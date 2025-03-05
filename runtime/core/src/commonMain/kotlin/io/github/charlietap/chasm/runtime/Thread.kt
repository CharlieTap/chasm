package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame

data class Thread(
    val frame: ActivationFrame,
    val instructions: Array<DispatchableInstruction>,
)
