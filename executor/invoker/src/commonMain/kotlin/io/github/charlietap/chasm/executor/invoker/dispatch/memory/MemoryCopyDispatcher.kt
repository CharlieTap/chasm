package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryCopyExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopy,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    MemoryCopyExecutor(vstack, context, instruction)
    nextIp
}
