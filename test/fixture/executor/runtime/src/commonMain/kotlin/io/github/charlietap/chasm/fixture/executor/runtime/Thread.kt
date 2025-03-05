package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame

fun thread(
    frame: ActivationFrame = frame(),
    instructions: Array<DispatchableInstruction> = emptyArray(),
): Thread = Thread(
    frame = frame,
    instructions = instructions,
)
