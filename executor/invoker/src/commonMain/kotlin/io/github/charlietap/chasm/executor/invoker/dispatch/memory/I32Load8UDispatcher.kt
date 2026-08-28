package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load8UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load8UDispatcher(
    instruction: MemoryInstruction.I32Load8U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Load8UExecutor(vstack, context, instruction)
    nextIp
}
