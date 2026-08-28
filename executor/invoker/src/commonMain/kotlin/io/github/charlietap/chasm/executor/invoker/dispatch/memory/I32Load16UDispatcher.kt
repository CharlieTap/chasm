package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Load16UDispatcher(
    instruction: MemoryInstruction.I32Load16U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I32Load16UExecutor(vstack, context, instruction)
    nextIp
}
