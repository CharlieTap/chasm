package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I64Load8UDispatcher(
    instruction: MemoryInstruction.I64Load8U,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    I64Load8UExecutor(vstack, context, instruction)
    nextIp
}
