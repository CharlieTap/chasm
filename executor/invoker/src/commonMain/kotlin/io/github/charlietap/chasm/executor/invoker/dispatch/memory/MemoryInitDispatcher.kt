package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInit,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    MemoryInitExecutor(vstack, context, instruction)
    nextIp
}
