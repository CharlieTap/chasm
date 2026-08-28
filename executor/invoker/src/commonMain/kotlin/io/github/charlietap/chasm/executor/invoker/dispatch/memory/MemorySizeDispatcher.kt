package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemorySizeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemorySizeDispatcher(
    instruction: MemoryInstruction.MemorySize,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    MemorySizeExecutor(vstack, context, instruction)
    nextIp
}
