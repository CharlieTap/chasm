package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun F32LoadDispatcher(
    instruction: MemoryInstruction.F32Load,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    F32LoadExecutor(vstack, context, instruction)
    nextIp
}
