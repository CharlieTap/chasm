package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame

fun thread(
    frame: ActivationFrame = frame(),
    instructions: Array<DispatchableInstruction> = emptyArray(),
): Thread = Thread(
    frame = frame,
    instructions = instructions,
)
