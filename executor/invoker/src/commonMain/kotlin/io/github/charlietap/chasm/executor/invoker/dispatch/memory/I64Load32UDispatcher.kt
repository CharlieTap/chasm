package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load32UDispatcher(
    instruction: MemoryInstruction.I64Load32U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load32UExecutor(vstack, context, instruction)
    nextIp
}
