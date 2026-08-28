package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load16UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load16UDispatcher(
    instruction: MemoryInstruction.I64Load16U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load16UExecutor(vstack, context, instruction)
    nextIp
}
