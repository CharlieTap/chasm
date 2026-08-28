package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryGrowExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryGrowDispatcher(
    instruction: MemoryInstruction.MemoryGrow,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    MemoryGrowExecutor(vstack, context, instruction)
    nextIp
}
