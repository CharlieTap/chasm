package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryFillExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFill,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    MemoryFillExecutor(vstack, context, instruction)
    nextIp
}
