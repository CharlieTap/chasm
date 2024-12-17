package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction

fun thread(
    frame: Stack.Entry.ActivationFrame = frame(),
    instructions: Array<DispatchableInstruction> = emptyArray(),
): Thread = Thread(
    frame = frame,
    instructions = instructions,
)
