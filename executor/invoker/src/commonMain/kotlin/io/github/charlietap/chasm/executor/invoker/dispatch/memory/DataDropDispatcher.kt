package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.DataDropExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun DataDropDispatcher(
    instruction: MemoryInstruction.DataDrop,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    DataDropExecutor(vstack, context, instruction)
    nextIp
}
