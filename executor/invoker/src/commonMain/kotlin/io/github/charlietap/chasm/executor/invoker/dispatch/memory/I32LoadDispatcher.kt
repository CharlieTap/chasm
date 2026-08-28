package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32LoadDispatcher(
    instruction: MemoryInstruction.I32Load,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32LoadExecutor(vstack, context, instruction)
    nextIp
}
